package com.company.intranet.hub;

import com.company.intranet.common.exception.BadRequestException;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.storage.StorageProperties;
import com.company.intranet.storage.StorageService;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.hub.dto.*;
import com.company.intranet.notification.events.EventCreatedEvent;
import com.company.intranet.notification.events.NewsPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HubService {

    private static final long        MAX_IMAGE_BYTES  = 5L * 1024 * 1024; // 5 MB decoded
    private static final Set<String> ALLOWED_MIME     = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    private final NewsPostRepository       newsPostRepository;
    private final EventRepository          eventRepository;
    private final EventRsvpRepository      eventRsvpRepository;
    private final EmployeeRepository       employeeRepository;
    private final HubMapper                hubMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final StorageService           storageService;
    private final StorageProperties        storageProps;

    // ── News ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public NewsListDto getNews(int page, int size, boolean adminView) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("pinned"), Sort.Order.desc("publishedAt")));

        Page<NewsPost> result = adminView
                ? newsPostRepository.findAll(pageable)
                : newsPostRepository.findByPublishedAtIsNotNull(pageable);

        List<NewsPostDto> content = hubMapper.toDtos(result.getContent());
        return new NewsListDto(content, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public NewsPostDetailDto getNewsById(UUID id, boolean adminView) {
        NewsPost post = newsPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News post not found"));

        if (!adminView && post.getPublishedAt() == null) {
            throw new ResourceNotFoundException("News post not found");
        }
        return hubMapper.toDetailDto(post);
    }

    @Transactional
    public NewsPostDetailDto createNews(CreateNewsRequest request, Employee author) {
        validateImage(request.coverImageData(), request.coverImageType());

        Instant publishedAt = request.published() ? Instant.now() : null;

        NewsPost post = NewsPost.builder()
                .title(request.title())
                .body(request.body())
                .pinned(request.pinned())
                .category(request.category())
                .author(author)
                .publishedAt(publishedAt)
                .build();

        NewsPost saved = newsPostRepository.save(post);

        if (request.coverImageData() != null && !request.coverImageData().isBlank()) {
            saved.setCoverImagePath(uploadNewsImage(saved.getId(), request.coverImageData(), request.coverImageType()));
            saved = newsPostRepository.save(saved);
        }

        if (request.published()) {
            String publishedDate = saved.getPublishedAt()
                    .atZone(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));
            String rawBody  = post.getBody() == null ? "" : post.getBody();
            String stripped = rawBody.replaceAll("<[^>]*>", "").strip();
            String excerpt  = stripped.length() > 200 ? stripped.substring(0, 200) + "…" : stripped;

            String coverImageUrl = saved.getCoverImagePath() != null
                    ? storageService.getSignedUrl(storageProps.getBucket().getNewsCovers(), saved.getCoverImagePath())
                    : "";
            eventPublisher.publishEvent(new NewsPublishedEvent(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getAuthor().getFullName(),
                    saved.getAuthor().getInitials(),
                    coverImageUrl,
                    publishedDate,
                    excerpt,
                    employeeRepository.findAllActiveEmails()));
        }

        return hubMapper.toDetailDto(saved);
    }

    @Transactional
    public NewsPostDetailDto updateNews(UUID id, UpdateNewsRequest request) {
        NewsPost post = newsPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News post not found"));

        validateImage(request.coverImageData(), request.coverImageType());

        post.setTitle(request.title());
        post.setBody(request.body());
        post.setPinned(request.pinned());

        if (request.coverImageData() != null && !request.coverImageData().isBlank()) {
            if (post.getCoverImagePath() != null) {
                storageService.delete(storageProps.getBucket().getNewsCovers(), post.getCoverImagePath());
            }
            post.setCoverImagePath(uploadNewsImage(post.getId(), request.coverImageData(), request.coverImageType()));
        } else if (request.coverImageData() != null) {
            // empty string means "remove image"
            if (post.getCoverImagePath() != null) {
                storageService.delete(storageProps.getBucket().getNewsCovers(), post.getCoverImagePath());
            }
            post.setCoverImagePath(null);
        }

        return hubMapper.toDetailDto(newsPostRepository.save(post));
    }

    @Transactional
    public NewsPostDetailDto publishNews(UUID id, boolean publish) {
        NewsPost post = newsPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News post not found"));

        boolean wasUnpublished = post.getPublishedAt() == null;

        post.setPublishedAt(publish ? Instant.now() : null);
        NewsPost saved = newsPostRepository.save(post);

        if (publish && wasUnpublished) {
            String publishedDate = saved.getPublishedAt()
                    .atZone(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));
            String rawBody  = post.getBody() == null ? "" : post.getBody();
            String stripped = rawBody.replaceAll("<[^>]*>", "").strip();
            String excerpt  = stripped.length() > 200 ? stripped.substring(0, 200) + "…" : stripped;
            String coverImageUrl = saved.getCoverImagePath() != null
                    ? storageService.getSignedUrl(storageProps.getBucket().getNewsCovers(), saved.getCoverImagePath())
                    : "";

            eventPublisher.publishEvent(new NewsPublishedEvent(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getAuthor().getFullName(),
                    saved.getAuthor().getInitials(),
                    coverImageUrl,
                    publishedDate,
                    excerpt,
                    employeeRepository.findAllActiveEmails()));
        }

        return hubMapper.toDetailDto(saved);
    }

    @Transactional
    public void deleteNews(UUID id) {
        NewsPost post = newsPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News post not found"));
        if (post.getCoverImagePath() != null) {
            storageService.delete(storageProps.getBucket().getNewsCovers(), post.getCoverImagePath());
        }
        newsPostRepository.delete(post);
    }

    // ── Events ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<EventDto> getUpcomingEvents(Employee employee) {
        List<Event> events = eventRepository.findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate.now());
        return hubMapper.toEventDtos(events, buildRsvpMap(events, employee));
    }

    @Transactional(readOnly = true)
    public List<EventDto> getAttendingEvents(Employee employee) {
        List<Event> events = eventRepository.findAttendingEvents(employee.getId(), LocalDate.now());
        return hubMapper.toEventDtos(events, buildRsvpMap(events, employee));
    }

    @Transactional(readOnly = true)
    public EventDto getEventById(UUID id, Employee employee) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        String myRsvpStatus = eventRsvpRepository
                .findByEventAndEmployee(id, employee.getId())
                .map(r -> r.getStatus().name())
                .orElse(null);
        return hubMapper.toEventDto(event, myRsvpStatus);
    }

    private Map<UUID, String> buildRsvpMap(List<Event> events, Employee employee) {
        if (events.isEmpty()) return Map.of();
        List<UUID> eventIds = events.stream().map(Event::getId).toList();
        return eventRsvpRepository.findByEmployeeAndEventIds(employee.getId(), eventIds)
                .stream()
                .collect(Collectors.toMap(r -> r.getEvent().getId(), r -> r.getStatus().name()));
    }

    @Transactional
    public EventDto createEvent(CreateEventRequest request, Employee author) {
        validateEventDates(request.eventDate(), request.endDate());

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .eventDate(request.eventDate())
                .endDate(request.endDate())
                .allDay(request.allDay())
                .startTime(request.allDay() ? null : request.startTime())
                .endTime(request.allDay() ? null : request.endTime())
                .author(author)
                .build();

        Event saved = eventRepository.save(event);

        List<String> allEmails = employeeRepository.findAllActiveEmails();
        String formattedDate = saved.getEventDate()
                .format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));
        eventPublisher.publishEvent(new EventCreatedEvent(
                saved.getTitle(),
                formattedDate,
                saved.getStartTime() != null ? saved.getStartTime() : "",
                saved.getEndTime() != null ? saved.getEndTime() : "",
                saved.getLocation() != null ? saved.getLocation() : "",
                saved.getDescription() != null ? saved.getDescription() : "",
                author.getFullName(),
                allEmails));

        return hubMapper.toEventDto(saved, null);
    }

    @Transactional
    public EventDto updateEvent(UUID id, UpdateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        validateEventDates(request.eventDate(), request.endDate());

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setEventDate(request.eventDate());
        event.setEndDate(request.endDate());
        event.setAllDay(request.allDay());
        event.setStartTime(request.allDay() ? null : request.startTime());
        event.setEndTime(request.allDay() ? null : request.endTime());

        return hubMapper.toEventDto(eventRepository.save(event), null);
    }

    @Transactional
    public void deleteEvent(UUID id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    // ── Validation helpers ────────────────────────────────────────────────────

    private void validateImage(String imageData, String imageType) {
        if (imageData == null || imageData.isBlank()) return;

        if (imageType == null || !ALLOWED_MIME.contains(imageType.toLowerCase())) {
            throw new BadRequestException(
                    "Unsupported image type. Allowed: image/jpeg, image/png, image/gif, image/webp");
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(imageData);
            if (decoded.length > MAX_IMAGE_BYTES) {
                throw new BadRequestException("Image exceeds 5 MB limit");
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid base64 image data");
        }
    }

    private String uploadNewsImage(UUID newsId, String base64Data, String contentType) {
        String path = newsId.toString();
        byte[] decoded = Base64.getDecoder().decode(base64Data);
        storageService.upload(storageProps.getBucket().getNewsCovers(), path, decoded, contentType);
        return path;
    }

    private void validateEventDates(LocalDate eventDate, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(eventDate)) {
            throw new BadRequestException("End date must not be before event date");
        }
    }
}

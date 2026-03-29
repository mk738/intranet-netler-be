package com.company.intranet.hub;

import com.company.intranet.common.exception.BadRequestException;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeProfile;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.hub.dto.*;
import com.company.intranet.notification.events.NewsPublishedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HubServiceTest {

    @Mock NewsPostRepository    newsPostRepository;
    @Mock EventRepository       eventRepository;
    @Mock EventRsvpRepository   eventRsvpRepository;
    @Mock EmployeeRepository    employeeRepository;
    @Mock HubMapper             hubMapper;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock com.company.intranet.config.FirebaseStorageService storageService;

    @InjectMocks HubService hubService;

    private Employee admin() {
        EmployeeProfile profile = EmployeeProfile.builder()
                .firstName("Anna").lastName("Admin").build();
        Employee emp = Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
        emp.setProfile(profile);
        profile.setEmployee(emp);
        return emp;
    }

    private NewsPost unpublishedPost(Employee author) {
        return NewsPost.builder()
                .id(UUID.randomUUID())
                .title("Test Post")
                .body("Body text")
                .author(author)
                .pinned(false)
                .build();
    }

    // ── createNews ────────────────────────────────────────────────────────────

    @Test
    void createNews_noImage_savesPost() {
        Employee author = admin();
        CreateNewsRequest req = new CreateNewsRequest("Title", "Body", false, false, null, null, null);

        NewsPost saved = unpublishedPost(author);
        when(newsPostRepository.save(any())).thenReturn(saved);
        when(hubMapper.toDetailDto(saved)).thenReturn(
                new NewsPostDetailDto(saved.getId(), "Title", "Body",
                        "Anna Admin", "AA", null, false, null, null, null));

        NewsPostDetailDto result = hubService.createNews(req, author);

        assertThat(result.title()).isEqualTo("Title");
        verify(newsPostRepository).save(any());
    }

    @Test
    void createNews_invalidMimeType_throwsBadRequest() {
        Employee author = admin();
        String fakeBase64 = Base64.getEncoder().encodeToString("fake".getBytes());
        CreateNewsRequest req = new CreateNewsRequest("T", "B", false, false, null, fakeBase64, "application/pdf");

        assertThatThrownBy(() -> hubService.createNews(req, author))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unsupported image type");
    }

    @Test
    void createNews_imageTooLarge_throwsBadRequest() {
        Employee author = admin();
        byte[] bigImage = new byte[6 * 1024 * 1024]; // 6 MB
        String largeBase64 = Base64.getEncoder().encodeToString(bigImage);
        CreateNewsRequest req = new CreateNewsRequest("T", "B", false, false, null, largeBase64, "image/jpeg");

        assertThatThrownBy(() -> hubService.createNews(req, author))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Image exceeds 5 MB limit");
    }

    @Test
    void createNews_invalidBase64_throwsBadRequest() {
        Employee author = admin();
        CreateNewsRequest req = new CreateNewsRequest("T", "B", false, false, null, "!!!notBase64!!!", "image/png");

        assertThatThrownBy(() -> hubService.createNews(req, author))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid base64 image data");
    }

    // ── publishNews ───────────────────────────────────────────────────────────

    @Test
    void publishNews_unpublishedPost_setsPublishedAtAndFiresEvent() {
        Employee author = admin();
        NewsPost post = unpublishedPost(author);

        when(newsPostRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(newsPostRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(employeeRepository.findAll()).thenReturn(List.of(author));
        when(hubMapper.toDetailDto(any())).thenReturn(
                new NewsPostDetailDto(post.getId(), "Test Post", "Body text",
                        "Anna Admin", "AA", Instant.now(), false, null, null, null));

        hubService.publishNews(post.getId(), true);

        ArgumentCaptor<NewsPublishedEvent> captor = ArgumentCaptor.forClass(NewsPublishedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().newsTitle()).isEqualTo("Test Post");
        assertThat(captor.getValue().recipientEmails()).contains("admin@x.com");
    }

    @Test
    void publishNews_alreadyPublished_doesNotFireEvent() {
        Employee author = admin();
        NewsPost post = unpublishedPost(author);
        post.setPublishedAt(Instant.now().minusSeconds(3600));

        when(newsPostRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(newsPostRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(hubMapper.toDetailDto(any())).thenReturn(
                new NewsPostDetailDto(post.getId(), "Test Post", "Body text",
                        "Anna Admin", "AA", post.getPublishedAt(), false, null, null, null));

        hubService.publishNews(post.getId(), true);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void publishNews_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(newsPostRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hubService.publishNews(id, true))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── deleteNews ────────────────────────────────────────────────────────────

    @Test
    void deleteNews_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(newsPostRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hubService.deleteNews(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getNews ───────────────────────────────────────────────────────────────

    @Test
    void getNews_adminView_returnsAllPosts() {
        Employee author = admin();
        NewsPost post = unpublishedPost(author);
        NewsPostDto dto = new NewsPostDto(post.getId(), "Test Post", "Anna Admin",
                "AA", null, false, false, null, null);

        when(newsPostRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(post)));
        when(hubMapper.toDtos(List.of(post))).thenReturn(List.of(dto));

        NewsListDto result = hubService.getNews(0, 10, true);

        assertThat(result.content()).hasSize(1);
        assertThat(result.total()).isEqualTo(1);
    }

    // ── createEvent ───────────────────────────────────────────────────────────

    @Test
    void createEvent_endBeforeStart_throwsBadRequest() {
        Employee author = admin();
        LocalDate start = LocalDate.now().plusDays(5);
        CreateEventRequest req = new CreateEventRequest(
                "Conference", null, null, start, start.minusDays(1), true, null, null);

        assertThatThrownBy(() -> hubService.createEvent(req, author))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("End date must not be before event date");
    }

    @Test
    void createEvent_success_returnsDto() {
        Employee author = admin();
        LocalDate start = LocalDate.now().plusDays(7);
        CreateEventRequest req = new CreateEventRequest(
                "Team Meeting", "Quarterly sync", "HQ", start, null, true, null, null);

        Event saved = Event.builder()
                .id(UUID.randomUUID()).title("Team Meeting").description("Quarterly sync")
                .location("HQ").eventDate(start).allDay(true).author(author).build();

        when(eventRepository.save(any())).thenReturn(saved);
        when(hubMapper.toEventDto(saved, null)).thenReturn(
                new EventDto(saved.getId(), "Team Meeting", "Quarterly sync",
                        "HQ", start, null, true, null, null, "Anna Admin", null, null));

        EventDto result = hubService.createEvent(req, author);

        assertThat(result.title()).isEqualTo("Team Meeting");
        assertThat(result.location()).isEqualTo("HQ");
    }

    // ── deleteEvent ───────────────────────────────────────────────────────────

    @Test
    void deleteEvent_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> hubService.deleteEvent(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

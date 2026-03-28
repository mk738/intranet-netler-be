package com.company.intranet.hub;

import com.company.intranet.hub.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class HubMapper {

    public NewsPostDto toDto(NewsPost post) {
        return new NewsPostDto(
                post.getId(),
                post.getTitle(),
                post.getAuthor().getFullName(),
                post.getAuthor().getInitials(),
                post.getPublishedAt(),
                post.isPinned(),
                post.getCoverImageData() != null,
                post.getCreatedAt(),
                post.getCategory()
        );
    }

    public NewsPostDetailDto toDetailDto(NewsPost post) {
        return new NewsPostDetailDto(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getAuthor().getFullName(),
                post.getAuthor().getInitials(),
                post.getPublishedAt(),
                post.isPinned(),
                post.getCoverImageData(),
                post.getCoverImageType(),
                post.getCreatedAt(),
                post.getCategory()
        );
    }

    public List<NewsPostDto> toDtos(List<NewsPost> posts) {
        return posts.stream().map(this::toDto).toList();
    }

    public EventDto toEventDto(Event event, String myRsvpStatus) {
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getEventDate(),
                event.getEndDate(),
                event.isAllDay(),
                event.getStartTime(),
                event.getEndTime(),
                event.getAuthor().getFullName(),
                event.getCreatedAt() != null ? event.getCreatedAt().toString() : null,
                myRsvpStatus
        );
    }

    public EventDto toEventDto(Event event) {
        return toEventDto(event, null);
    }

    public List<EventDto> toEventDtos(List<Event> events, Map<UUID, String> rsvpStatusByEventId) {
        return events.stream()
                .map(e -> toEventDto(e, rsvpStatusByEventId.get(e.getId())))
                .toList();
    }

    public List<EventDto> toEventDtos(List<Event> events) {
        return events.stream().map(this::toEventDto).toList();
    }
}

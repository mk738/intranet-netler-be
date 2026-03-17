package com.company.intranet.hub;

import com.company.intranet.hub.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HubMapper {

    public NewsPostDto toDto(NewsPost post) {
        return new NewsPostDto(
                post.getId(),
                post.getTitle(),
                post.getAuthor().getFullName(),
                post.getAuthor().getInitials(),
                post.getPublishedAt() != null ? post.getPublishedAt().toString() : null,
                post.isPinned(),
                post.getCoverImageData() != null,
                post.getCreatedAt() != null ? post.getCreatedAt().toString() : null
        );
    }

    public NewsPostDetailDto toDetailDto(NewsPost post) {
        return new NewsPostDetailDto(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getAuthor().getFullName(),
                post.getAuthor().getInitials(),
                post.getPublishedAt() != null ? post.getPublishedAt().toString() : null,
                post.isPinned(),
                post.getCoverImageData(),
                post.getCoverImageType(),
                post.getCreatedAt() != null ? post.getCreatedAt().toString() : null
        );
    }

    public List<NewsPostDto> toDtos(List<NewsPost> posts) {
        return posts.stream().map(this::toDto).toList();
    }

    public EventDto toEventDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getEventDate(),
                event.getEndDate(),
                event.isAllDay(),
                event.getAuthor().getFullName(),
                event.getCreatedAt() != null ? event.getCreatedAt().toString() : null
        );
    }

    public List<EventDto> toEventDtos(List<Event> events) {
        return events.stream().map(this::toEventDto).toList();
    }
}

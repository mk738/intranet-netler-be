package com.company.intranet.vacation;

import com.company.intranet.vacation.dto.VacationDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VacationMapper {

    public VacationDto toDto(VacationRequest request) {
        return new VacationDto(
                request.getId(),
                request.getEmployee().getId(),
                request.getEmployee().getFullName(),
                request.getEmployee().getInitials(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDaysCount(),
                request.getStatus().name(),
                request.getReviewedBy() != null
                        ? request.getReviewedBy().getFullName() : null,
                request.getReviewedAt() != null
                        ? request.getReviewedAt().toString() : null,
                request.getCreatedAt() != null
                        ? request.getCreatedAt().toString() : null
        );
    }

    public List<VacationDto> toDtos(List<VacationRequest> requests) {
        return requests.stream().map(this::toDto).toList();
    }
}

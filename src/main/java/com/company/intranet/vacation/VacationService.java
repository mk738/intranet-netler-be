package com.company.intranet.vacation;

import com.company.intranet.common.exception.BadRequestException;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.notification.events.VacationRequestedEvent;
import com.company.intranet.notification.events.VacationReviewedEvent;
import com.company.intranet.vacation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository    vacationRepository;
    private final EmployeeRepository    employeeRepository;
    private final VacationMapper        vacationMapper;
    private final ApplicationEventPublisher eventPublisher;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String formatDateRange(LocalDate start, LocalDate end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
        return start.format(fmt) + " – " + end.format(fmt);
    }

    int calculateBusinessDays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate current = start;
        while (!current.isAfter(end)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }

    // ── Employee methods ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<VacationDto> getMyVacations(Employee me) {
        return vacationMapper.toDtos(
                vacationRepository.findByEmployeeOrderByCreatedAtDesc(me));
    }

    @Transactional
    public VacationDto submitVacation(SubmitVacationRequest request, Employee me) {
        LocalDate startDate = request.startDate();
        LocalDate endDate   = request.endDate();

        if (startDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be in the past");
        }
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date must be after start date");
        }

        boolean overlaps = vacationRepository
                .existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNot(
                        me, endDate, startDate, VacationRequest.VacationStatus.REJECTED);
        if (overlaps) {
            throw new BadRequestException(
                    "You already have a vacation request overlapping these dates");
        }

        VacationRequest vacation = VacationRequest.builder()
                .employee(me)
                .startDate(startDate)
                .endDate(endDate)
                .daysCount(calculateBusinessDays(startDate, endDate))
                .status(VacationRequest.VacationStatus.PENDING)
                .build();

        VacationRequest saved = vacationRepository.save(vacation);

        List<String> adminEmails = employeeRepository.findAllAdminEmails();
        eventPublisher.publishEvent(new VacationRequestedEvent(
                me.getFullName(),
                me.getEmail(),
                formatDateRange(startDate, endDate),
                adminEmails));

        return vacationMapper.toDto(saved);
    }

    @Transactional
    public void cancelVacation(UUID id, Employee me) {
        VacationRequest vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation request not found"));

        if (!vacation.getEmployee().getId().equals(me.getId())) {
            throw new BadRequestException("You can only cancel your own requests");
        }
        if (vacation.getStatus() != VacationRequest.VacationStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be cancelled");
        }

        vacationRepository.delete(vacation);
    }

    // ── Admin methods ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<VacationDto> getAllVacations(String statusFilter) {
        VacationRequest.VacationStatus status = null;

        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                status = VacationRequest.VacationStatus.valueOf(statusFilter.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status filter: " + statusFilter);
            }
        }

        return vacationMapper.toDtos(vacationRepository.findAllWithEmployee(status));
    }

    @Transactional(readOnly = true)
    public VacationSummaryDto getSummary() {
        return new VacationSummaryDto(
                vacationRepository.countByStatus(VacationRequest.VacationStatus.PENDING),
                vacationRepository.countByStatus(VacationRequest.VacationStatus.APPROVED),
                vacationRepository.countByStatus(VacationRequest.VacationStatus.REJECTED)
        );
    }

    @Transactional
    public VacationDto reviewVacation(UUID id, ReviewVacationRequest review, Employee admin) {
        VacationRequest vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation request not found"));

        if (vacation.getStatus() != VacationRequest.VacationStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be reviewed");
        }

        vacation.setStatus(review.approved()
                ? VacationRequest.VacationStatus.APPROVED
                : VacationRequest.VacationStatus.REJECTED);
        vacation.setReviewedBy(admin);
        vacation.setReviewedAt(Instant.now());

        VacationRequest saved = vacationRepository.save(vacation);

        String outcome = review.approved() ? "approved" : "rejected";
        eventPublisher.publishEvent(new VacationReviewedEvent(
                vacation.getEmployee().getEmail(),
                vacation.getEmployee().getFullName(),
                formatDateRange(vacation.getStartDate(), vacation.getEndDate()),
                outcome));

        return vacationMapper.toDto(saved);
    }
}

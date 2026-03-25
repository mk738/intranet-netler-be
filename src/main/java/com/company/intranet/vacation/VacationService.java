package com.company.intranet.vacation;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.notification.events.VacationRequestedEvent;
import com.company.intranet.notification.events.VacationReviewedEvent;
import com.company.intranet.vacation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
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

    private static final int ANNUAL_ALLOWANCE = 25;

    private final VacationRepository        vacationRepository;
    private final EmployeeRepository        employeeRepository;
    private final VacationMapper            vacationMapper;
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
            throw new AppException(
                    ErrorCode.VACATION_PAST_DATE,
                    "Start date cannot be in the past.",
                    HttpStatus.BAD_REQUEST);
        }
        if (endDate.isBefore(startDate)) {
            throw new AppException(
                    ErrorCode.VACATION_DATE_INVALID,
                    "End date must be on or after start date.",
                    HttpStatus.BAD_REQUEST);
        }

        boolean overlaps = vacationRepository
                .existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatus(
                        me, endDate, startDate, VacationRequest.VacationStatus.APPROVED);
        if (overlaps) {
            throw new AppException(
                    ErrorCode.VACATION_OVERLAP,
                    "You already have a vacation request overlapping these dates.",
                    HttpStatus.CONFLICT);
        }

        int requestedDays = calculateBusinessDays(startDate, endDate);
        int usedDays      = vacationRepository.sumApprovedDaysForYear(me, startDate.getYear());
        if (usedDays + requestedDays > ANNUAL_ALLOWANCE) {
            throw new AppException(
                    ErrorCode.VACATION_INSUFFICIENT_DAYS,
                    "Insufficient vacation days. Allowance: " + ANNUAL_ALLOWANCE
                            + ", used: " + usedDays
                            + ", requested: " + requestedDays + ".",
                    HttpStatus.BAD_REQUEST);
        }

        VacationRequest vacation = VacationRequest.builder()
                .employee(me)
                .startDate(startDate)
                .endDate(endDate)
                .daysCount(requestedDays)
                .reason(request.reason())
                .status(VacationRequest.VacationStatus.PENDING)
                .build();

        VacationRequest saved = vacationRepository.save(vacation);

        DateTimeFormatter displayFmt = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        String jobTitle = (me.getProfile() != null && me.getProfile().getJobTitle() != null)
                ? me.getProfile().getJobTitle() : "";

        List<String> adminEmails = employeeRepository.findAllAdminEmails();
        eventPublisher.publishEvent(new VacationRequestedEvent(
                me.getFullName(),
                me.getEmail(),
                jobTitle,
                startDate.format(displayFmt),
                endDate.format(displayFmt),
                requestedDays,
                LocalDate.now().format(displayFmt),
                formatDateRange(startDate, endDate),
                adminEmails));

        return vacationMapper.toDto(saved);
    }

    @Transactional
    public void cancelVacation(UUID id, Employee me) {
        VacationRequest vacation = vacationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation request not found"));

        if (!vacation.getEmployee().getId().equals(me.getId())) {
            throw new AppException(
                    ErrorCode.BAD_REQUEST,
                    "You can only cancel your own requests.",
                    HttpStatus.BAD_REQUEST);
        }
        if (vacation.getStatus() != VacationRequest.VacationStatus.PENDING) {
            throw new AppException(
                    ErrorCode.BAD_REQUEST,
                    "Only pending requests can be cancelled.",
                    HttpStatus.BAD_REQUEST);
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
                throw new AppException(
                        ErrorCode.BAD_REQUEST,
                        "Invalid status filter: " + statusFilter,
                        HttpStatus.BAD_REQUEST);
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
            throw new AppException(
                    ErrorCode.BAD_REQUEST,
                    "Only pending requests can be reviewed.",
                    HttpStatus.BAD_REQUEST);
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

package com.company.intranet.crm;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.crm.dto.*;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrmService {

    private final ClientRepository     clientRepository;
    private final AssignmentRepository assignmentRepository;
    private final EmployeeRepository   employeeRepository;
    private final CrmMapper            crmMapper;

    // ── Placement view ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PlacementViewDto getPlacementView() {
        List<Assignment> activeAssignments =
                assignmentRepository.findAllActiveWithEmployeeAndClient();
        List<Employee> unplacedEmployees =
                employeeRepository.findAllWithNoActiveAssignment();

        Map<UUID, List<Assignment>> byClientId = activeAssignments.stream()
                .collect(Collectors.groupingBy(a -> a.getClient().getId()));

        List<ClientGroupDto> clientGroups = byClientId.entrySet().stream()
                .map(entry -> {
                    Client client = entry.getValue().get(0).getClient();
                    List<Assignment> assignments = entry.getValue();
                    return new ClientGroupDto(
                            client.getId(),
                            client.getCompanyName(),
                            client.getStatus().name(),
                            assignments.size(),
                            crmMapper.toAssignmentDtos(assignments)
                    );
                })
                .sorted(Comparator.comparing(ClientGroupDto::companyName))
                .toList();

        List<UnplacedDto> unplaced = unplacedEmployees.stream()
                .map(employee -> {
                    Assignment lastEnded = assignmentRepository
                            .findByEmployeeOrderByStartDateDesc(employee).stream()
                            .filter(a -> a.getStatus() == Assignment.AssignmentStatus.ENDED)
                            .findFirst()
                            .orElse(null);

                    String lastClient = lastEnded != null
                            ? lastEnded.getClient().getCompanyName() : null;
                    String lastDate   = lastEnded != null
                            ? lastEnded.getStartDate().toString() : null;

                    return crmMapper.toUnplacedDto(employee, lastClient, lastDate);
                })
                .toList();

        LocalDate threshold = LocalDate.now().plusDays(30);
        int endingSoon = (int) activeAssignments.stream()
                .filter(a -> a.getEndDate() != null && a.getEndDate().isBefore(threshold))
                .count();

        return new PlacementViewDto(
                clientGroups,
                unplaced,
                activeAssignments.size(),
                unplaced.size(),
                endingSoon,
                byClientId.size()
        );
    }

    // ── Assignments ───────────────────────────────────────────────────────────

    @Transactional
    public AssignmentDto createAssignment(CreateAssignmentRequest request) {
        boolean hasClientId  = request.clientId()  != null;
        boolean hasNewClient = request.newClient()  != null;

        if (!hasClientId && !hasNewClient) {
            throw new AppException(ErrorCode.BAD_REQUEST,
                    "Either clientId or newClient must be provided.", HttpStatus.BAD_REQUEST);
        }
        if (hasClientId && hasNewClient) {
            throw new AppException(ErrorCode.BAD_REQUEST,
                    "Provide either clientId or newClient, not both.", HttpStatus.BAD_REQUEST);
        }

        if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
            throw new AppException(
                    ErrorCode.ASSIGNMENT_DATE_INVALID,
                    "End date cannot be before start date.",
                    HttpStatus.BAD_REQUEST);
        }

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (assignmentRepository.existsByEmployeeAndStatus(
                employee, Assignment.AssignmentStatus.ACTIVE)) {
            throw new AppException(
                    ErrorCode.ASSIGNMENT_ALREADY_ACTIVE,
                    "This employee already has an active assignment.",
                    HttpStatus.CONFLICT);
        }

        Client client = hasClientId
                ? clientRepository.findById(request.clientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Client not found"))
                : clientRepository.save(crmMapper.toClient(request.newClient()));

        if (client.getStatus() != Client.ClientStatus.ACTIVE) {
            client.setStatus(Client.ClientStatus.ACTIVE);
            clientRepository.save(client);
        }

        Assignment assignment = Assignment.builder()
                .employee(employee)
                .client(client)
                .projectName(request.projectName())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(Assignment.AssignmentStatus.ACTIVE)
                .build();

        return crmMapper.toAssignmentDto(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentDto endAssignment(UUID assignmentId,
                                       com.company.intranet.crm.dto.EndAssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        if (assignment.getStatus() == Assignment.AssignmentStatus.ENDED) {
            throw new AppException(ErrorCode.BAD_REQUEST,
                    "Assignment is already ended.", HttpStatus.BAD_REQUEST);
        }

        LocalDate endDate = (request != null && request.endDate() != null)
                ? request.endDate()
                : LocalDate.now();

        assignment.setEndDate(endDate);

        if (!endDate.isAfter(LocalDate.now())) {
            // Date is today or in the past — end the assignment immediately
            assignment.setStatus(Assignment.AssignmentStatus.ENDED);
            assignmentRepository.save(assignment);

            // If the client has no remaining active assignments, mark them inactive
            Client client = assignment.getClient();
            boolean hasOtherActive = assignmentRepository
                    .existsByClientAndStatus(client, Assignment.AssignmentStatus.ACTIVE);
            if (!hasOtherActive) {
                client.setStatus(Client.ClientStatus.INACTIVE);
                clientRepository.save(client);
            }
            return crmMapper.toAssignmentDto(assignment);
        }
        // Date is in the future — keep status ACTIVE; computeStatus() will derive
        // ENDING_SOON automatically when the date falls within the 30-day window

        return crmMapper.toAssignmentDto(assignmentRepository.save(assignment));
    }

    // ── Clients ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ClientDto> getAllClients() {
        return crmMapper.toClientDtos(clientRepository.findAllByOrderByCompanyNameAsc());
    }

    @Transactional(readOnly = true)
    public ClientDto getClientById(UUID id) {
        return clientRepository.findById(id)
                .map(crmMapper::toClientDto)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    @Transactional
    public ClientDto createClient(NewClientDto request) {
        if (request.orgNumber() != null && !request.orgNumber().isBlank()
                && clientRepository.existsByOrgNumber(request.orgNumber())) {
            throw new AppException(
                    ErrorCode.CLIENT_ORG_NUMBER_TAKEN,
                    "A client with that organisation number already exists.",
                    HttpStatus.CONFLICT);
        }
        return crmMapper.toClientDto(clientRepository.save(crmMapper.toClient(request)));
    }

    @Transactional
    public ClientDto updateClient(UUID id, UpdateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        if (request.orgNumber() != null && !request.orgNumber().isBlank()
                && clientRepository.existsByOrgNumberAndIdNot(request.orgNumber(), id)) {
            throw new AppException(
                    ErrorCode.CLIENT_ORG_NUMBER_TAKEN,
                    "A client with that organisation number already exists.",
                    HttpStatus.CONFLICT);
        }

        client.setCompanyName(request.companyName());
        client.setContactName(request.contactName());
        client.setContactEmail(request.contactEmail());
        client.setPhone(request.phone());
        client.setOrgNumber(request.orgNumber());
        client.setStatus(request.status());

        return crmMapper.toClientDto(clientRepository.save(client));
    }
}

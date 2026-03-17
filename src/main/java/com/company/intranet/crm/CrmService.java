package com.company.intranet.crm;

import com.company.intranet.common.exception.BadRequestException;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.crm.dto.*;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
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

        // Group active assignments by client id, preserve the client reference
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
            throw new BadRequestException("Either clientId or newClient must be provided");
        }
        if (hasClientId && hasNewClient) {
            throw new BadRequestException("Provide either clientId or newClient, not both");
        }

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (assignmentRepository.existsByEmployeeAndStatus(
                employee, Assignment.AssignmentStatus.ACTIVE)) {
            throw new BadRequestException("Employee already has an active assignment");
        }

        Client client = hasClientId
                ? clientRepository.findById(request.clientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Client not found"))
                : clientRepository.save(crmMapper.toClient(request.newClient()));

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
    public AssignmentDto endAssignment(UUID assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        if (assignment.getStatus() == Assignment.AssignmentStatus.ENDED) {
            throw new BadRequestException("Assignment is already ended");
        }

        assignment.setStatus(Assignment.AssignmentStatus.ENDED);
        if (assignment.getEndDate() == null) {
            assignment.setEndDate(LocalDate.now());
        }

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
        return crmMapper.toClientDto(clientRepository.save(crmMapper.toClient(request)));
    }

    @Transactional
    public ClientDto updateClient(UUID id, UpdateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        client.setCompanyName(request.companyName());
        client.setContactName(request.contactName());
        client.setContactEmail(request.contactEmail());
        client.setPhone(request.phone());
        client.setOrgNumber(request.orgNumber());
        client.setStatus(request.status());

        return crmMapper.toClientDto(clientRepository.save(client));
    }
}

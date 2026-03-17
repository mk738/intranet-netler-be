package com.company.intranet.crm;

import com.company.intranet.crm.dto.*;
import com.company.intranet.employee.Employee;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CrmMapper {

    public ClientDto toClientDto(Client client) {
        return new ClientDto(
                client.getId(),
                client.getCompanyName(),
                client.getContactName(),
                client.getContactEmail(),
                client.getPhone(),
                client.getOrgNumber(),
                client.getStatus().name(),
                client.getCreatedAt() != null ? client.getCreatedAt().toString() : null
        );
    }

    public List<ClientDto> toClientDtos(List<Client> clients) {
        return clients.stream().map(this::toClientDto).toList();
    }

    public AssignmentDto toAssignmentDto(Assignment assignment) {
        Employee employee = assignment.getEmployee();
        Client   client   = assignment.getClient();
        String   jobTitle = employee.getProfile() != null
                ? employee.getProfile().getJobTitle()
                : null;

        return new AssignmentDto(
                assignment.getId(),
                employee.getId(),
                employee.getFullName(),
                employee.getInitials(),
                jobTitle,
                client.getId(),
                client.getCompanyName(),
                assignment.getProjectName(),
                assignment.getStartDate(),
                assignment.getEndDate(),
                computeStatus(assignment)
        );
    }

    public List<AssignmentDto> toAssignmentDtos(List<Assignment> assignments) {
        return assignments.stream().map(this::toAssignmentDto).toList();
    }

    public UnplacedDto toUnplacedDto(Employee employee,
                                     String lastPlacedClient,
                                     String lastPlacedDate) {
        String jobTitle = employee.getProfile() != null
                ? employee.getProfile().getJobTitle()
                : null;
        return new UnplacedDto(
                employee.getId(),
                employee.getFullName(),
                employee.getInitials(),
                jobTitle,
                lastPlacedClient,
                lastPlacedDate
        );
    }

    public Client toClient(NewClientDto dto) {
        return Client.builder()
                .companyName(dto.companyName())
                .orgNumber(dto.orgNumber())
                .contactName(dto.contactName())
                .contactEmail(dto.contactEmail())
                .status(dto.status() != null ? dto.status() : Client.ClientStatus.ACTIVE)
                .build();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String computeStatus(Assignment assignment) {
        if (assignment.getStatus() == Assignment.AssignmentStatus.ENDED) {
            return "ENDED";
        }
        if (assignment.getEndDate() != null
                && assignment.getEndDate().isBefore(LocalDate.now().plusDays(30))) {
            return "ENDING_SOON";
        }
        return "ACTIVE";
    }
}

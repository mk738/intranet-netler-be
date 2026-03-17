package com.company.intranet.crm;

import com.company.intranet.common.exception.BadRequestException;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.crm.dto.*;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeProfile;
import com.company.intranet.employee.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmServiceTest {

    @Mock ClientRepository     clientRepository;
    @Mock AssignmentRepository assignmentRepository;
    @Mock EmployeeRepository   employeeRepository;
    @Mock CrmMapper            crmMapper;

    @InjectMocks CrmService crmService;

    // ── createAssignment validations ──────────────────────────────────────────

    @Test
    void createAssignment_neitherClientIdNorNewClient_throwsBadRequest() {
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                UUID.randomUUID(), null, null,
                "Project X", LocalDate.now(), null);

        assertThatThrownBy(() -> crmService.createAssignment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Either clientId or newClient must be provided");
    }

    @Test
    void createAssignment_bothClientIdAndNewClient_throwsBadRequest() {
        NewClientDto newClient = new NewClientDto(
                "ACME", null, null, null, Client.ClientStatus.ACTIVE);

        CreateAssignmentRequest request = new CreateAssignmentRequest(
                UUID.randomUUID(), UUID.randomUUID(), newClient,
                "Project X", LocalDate.now(), null);

        assertThatThrownBy(() -> crmService.createAssignment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not both");
    }

    @Test
    void createAssignment_unknownEmployee_throwsResourceNotFound() {
        UUID unknownId = UUID.randomUUID();
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                unknownId, UUID.randomUUID(), null,
                "Project X", LocalDate.now(), null);

        when(employeeRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crmService.createAssignment(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found");
    }

    @Test
    void createAssignment_employeeAlreadyActive_throwsBadRequest() {
        UUID empId = UUID.randomUUID();
        Employee employee = Employee.builder().id(empId).build();

        CreateAssignmentRequest request = new CreateAssignmentRequest(
                empId, UUID.randomUUID(), null,
                "Project X", LocalDate.now(), null);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(assignmentRepository.existsByEmployeeAndStatus(
                employee, Assignment.AssignmentStatus.ACTIVE)).thenReturn(true);

        assertThatThrownBy(() -> crmService.createAssignment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Employee already has an active assignment");
    }

    // ── endAssignment ─────────────────────────────────────────────────────────

    @Test
    void endAssignment_alreadyEnded_throwsBadRequest() {
        UUID id = UUID.randomUUID();
        Assignment ended = Assignment.builder()
                .id(id)
                .status(Assignment.AssignmentStatus.ENDED)
                .build();

        when(assignmentRepository.findById(id)).thenReturn(Optional.of(ended));

        assertThatThrownBy(() -> crmService.endAssignment(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Assignment is already ended");
    }

    @Test
    void endAssignment_nullEndDate_setsEndDateToToday() {
        UUID id = UUID.randomUUID();

        EmployeeProfile profile = EmployeeProfile.builder()
                .firstName("Erik").lastName("L").build();
        Employee employee = Employee.builder()
                .id(UUID.randomUUID()).email("e@x.com")
                .role(Employee.Role.EMPLOYEE).build();
        employee.setProfile(profile);
        profile.setEmployee(employee);

        Client client = Client.builder()
                .id(UUID.randomUUID()).companyName("Acme").build();

        Assignment assignment = Assignment.builder()
                .id(id)
                .employee(employee)
                .client(client)
                .projectName("P")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(null)
                .status(Assignment.AssignmentStatus.ACTIVE)
                .build();

        when(assignmentRepository.findById(id)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AssignmentDto dto = new AssignmentDto(id, employee.getId(), "Erik L", "EL",
                null, client.getId(), "Acme", "P",
                LocalDate.of(2024, 1, 1), LocalDate.now(), "ENDED");
        when(crmMapper.toAssignmentDto(any())).thenReturn(dto);

        crmService.endAssignment(id);

        assertThat(assignment.getEndDate()).isEqualTo(LocalDate.now());
        assertThat(assignment.getStatus()).isEqualTo(Assignment.AssignmentStatus.ENDED);
    }

    // ── getPlacementView ──────────────────────────────────────────────────────

    @Test
    void getPlacementView_correctlySeparatesPlacedAndUnplaced() {
        Employee placed   = Employee.builder().id(UUID.randomUUID()).build();
        Employee unplaced = Employee.builder().id(UUID.randomUUID()).build();
        Client   client   = Client.builder().id(UUID.randomUUID())
                .companyName("Spotify").status(Client.ClientStatus.ACTIVE).build();

        Assignment active = Assignment.builder()
                .id(UUID.randomUUID())
                .employee(placed)
                .client(client)
                .projectName("P")
                .startDate(LocalDate.of(2025, 1, 1))
                .status(Assignment.AssignmentStatus.ACTIVE)
                .build();

        when(assignmentRepository.findAllActiveWithEmployeeAndClient())
                .thenReturn(List.of(active));
        when(employeeRepository.findAllWithNoActiveAssignment())
                .thenReturn(List.of(unplaced));
        when(assignmentRepository.findByEmployeeOrderByStartDateDesc(unplaced))
                .thenReturn(List.of());

        AssignmentDto assignmentDto = new AssignmentDto(active.getId(), placed.getId(),
                "", "", null, client.getId(), "Spotify", "P",
                LocalDate.of(2025, 1, 1), null, "ACTIVE");
        when(crmMapper.toAssignmentDtos(any())).thenReturn(List.of(assignmentDto));
        when(crmMapper.toUnplacedDto(eq(unplaced), isNull(), isNull()))
                .thenReturn(new UnplacedDto(unplaced.getId(), "", "", null, null, null));

        PlacementViewDto view = crmService.getPlacementView();

        assertThat(view.totalPlaced()).isEqualTo(1);
        assertThat(view.totalUnplaced()).isEqualTo(1);
        assertThat(view.totalActiveClients()).isEqualTo(1);
        assertThat(view.clientGroups()).hasSize(1);
        assertThat(view.unplaced()).hasSize(1);
    }
}

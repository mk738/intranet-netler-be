package com.company.intranet.employee;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.employee.dto.EmployeeHardwareDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeHardwareServiceTest {

    @Mock EmployeeHardwareRepository hardwareRepository;

    @InjectMocks EmployeeHardwareService hardwareService;

    private static final UUID EMP_ID      = UUID.randomUUID();
    private static final UUID HARDWARE_ID = UUID.randomUUID();

    private EmployeeHardware buildHardware(UUID id, UUID employeeId, String name) {
        EmployeeHardware h = new EmployeeHardware();
        h.setId(id);
        h.setEmployeeId(employeeId);
        h.setName(name);
        return h;
    }

    // ── getHardware ───────────────────────────────────────────────────────────

    @Test
    void getHardware_returnsAllItemsForEmployee() {
        EmployeeHardware h1 = buildHardware(UUID.randomUUID(), EMP_ID, "MacBook Pro");
        EmployeeHardware h2 = buildHardware(UUID.randomUUID(), EMP_ID, "External Monitor");
        when(hardwareRepository.findByEmployeeIdOrderByCreatedAtAsc(EMP_ID))
                .thenReturn(List.of(h1, h2));

        List<EmployeeHardwareDto> result = hardwareService.getHardware(EMP_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("MacBook Pro");
        assertThat(result.get(1).name()).isEqualTo("External Monitor");
    }

    @Test
    void getHardware_noItems_returnsEmptyList() {
        when(hardwareRepository.findByEmployeeIdOrderByCreatedAtAsc(EMP_ID))
                .thenReturn(List.of());

        List<EmployeeHardwareDto> result = hardwareService.getHardware(EMP_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void getHardware_mapsCreatedAtFromEntity() {
        Instant now = Instant.now();
        EmployeeHardware h = buildHardware(HARDWARE_ID, EMP_ID, "Headset");
        ReflectionTestUtils.setField(h, "createdAt", now);
        when(hardwareRepository.findByEmployeeIdOrderByCreatedAtAsc(EMP_ID))
                .thenReturn(List.of(h));

        List<EmployeeHardwareDto> result = hardwareService.getHardware(EMP_ID);

        assertThat(result.get(0).createdAt()).isEqualTo(now);
        assertThat(result.get(0).id()).isEqualTo(HARDWARE_ID);
    }

    // ── addHardware ───────────────────────────────────────────────────────────

    @Test
    void addHardware_savesAndReturnsDto() {
        EmployeeHardware saved = buildHardware(HARDWARE_ID, EMP_ID, "Keyboard");
        when(hardwareRepository.save(any(EmployeeHardware.class))).thenReturn(saved);

        EmployeeHardwareDto result = hardwareService.addHardware(EMP_ID, "Keyboard");

        assertThat(result.id()).isEqualTo(HARDWARE_ID);
        assertThat(result.name()).isEqualTo("Keyboard");
        verify(hardwareRepository).save(argThat(h ->
                h.getEmployeeId().equals(EMP_ID) && h.getName().equals("Keyboard")));
    }

    @Test
    void addHardware_setsCorrectEmployeeId() {
        UUID otherId = UUID.randomUUID();
        EmployeeHardware saved = buildHardware(UUID.randomUUID(), otherId, "Mouse");
        when(hardwareRepository.save(any(EmployeeHardware.class))).thenReturn(saved);

        hardwareService.addHardware(otherId, "Mouse");

        verify(hardwareRepository).save(argThat(h -> h.getEmployeeId().equals(otherId)));
    }

    // ── removeHardware ────────────────────────────────────────────────────────

    @Test
    void removeHardware_validOwnership_deletesItem() {
        EmployeeHardware h = buildHardware(HARDWARE_ID, EMP_ID, "MacBook Pro");
        when(hardwareRepository.findById(HARDWARE_ID)).thenReturn(Optional.of(h));

        hardwareService.removeHardware(EMP_ID, HARDWARE_ID);

        verify(hardwareRepository).deleteByIdAndEmployeeId(HARDWARE_ID, EMP_ID);
    }

    @Test
    void removeHardware_notFound_throwsNotFound() {
        when(hardwareRepository.findById(HARDWARE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hardwareService.removeHardware(EMP_ID, HARDWARE_ID))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getCode())
                        .isEqualTo(ErrorCode.NOT_FOUND));

        verify(hardwareRepository, never()).deleteByIdAndEmployeeId(any(), any());
    }

    @Test
    void removeHardware_belongsToDifferentEmployee_throwsNotFound() {
        UUID otherEmpId = UUID.randomUUID();
        EmployeeHardware h = buildHardware(HARDWARE_ID, otherEmpId, "MacBook Pro");
        when(hardwareRepository.findById(HARDWARE_ID)).thenReturn(Optional.of(h));

        assertThatThrownBy(() -> hardwareService.removeHardware(EMP_ID, HARDWARE_ID))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getCode())
                        .isEqualTo(ErrorCode.NOT_FOUND));

        verify(hardwareRepository, never()).deleteByIdAndEmployeeId(any(), any());
    }
}

package com.company.intranet.employee;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.employee.dto.EmployeeHardwareDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeHardwareService {

    private final EmployeeHardwareRepository hardwareRepository;

    @Transactional(readOnly = true)
    public List<EmployeeHardwareDto> getHardware(UUID employeeId) {
        return hardwareRepository.findByEmployeeIdOrderByCreatedAtAsc(employeeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public EmployeeHardwareDto addHardware(UUID employeeId, String name) {
        EmployeeHardware hardware = EmployeeHardware.builder()
                .employeeId(employeeId)
                .name(name)
                .build();
        return toDto(hardwareRepository.save(hardware));
    }

    @Transactional
    public void removeHardware(UUID employeeId, UUID hardwareId) {
        hardwareRepository.findById(hardwareId)
                .filter(h -> h.getEmployeeId().equals(employeeId))
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Hardware item not found", HttpStatus.NOT_FOUND));
        hardwareRepository.deleteByIdAndEmployeeId(hardwareId, employeeId);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private EmployeeHardwareDto toDto(EmployeeHardware hardware) {
        return new EmployeeHardwareDto(
                hardware.getId(),
                hardware.getName(),
                hardware.getCreatedAt()
        );
    }
}

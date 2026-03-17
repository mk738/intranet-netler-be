package com.company.intranet.auth;

import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeMapper;
import com.company.intranet.employee.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeMapper employeeMapper;

    public EmployeeDto getCurrentUser(Employee employee) {
        return employeeMapper.toDto(employee);
    }
}

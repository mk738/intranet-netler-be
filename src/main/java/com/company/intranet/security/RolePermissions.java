package com.company.intranet.security;

import com.company.intranet.employee.Employee;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Single source of truth for which permissions each role carries.
 *
 * To add a new role: add it to {@link Employee.Role}, add an entry here,
 * and update {@link FirebaseTokenFilter} — no controller code needs to change.
 */
public final class RolePermissions {

    private RolePermissions() {}

    public static final Map<Employee.Role, Set<Permission>> MAP = Map.of(
            Employee.Role.SUPERADMIN, EnumSet.allOf(Permission.class),
            Employee.Role.ADMIN,      EnumSet.complementOf(EnumSet.of(
                                          Permission.EMPLOYEE_TERMINATE,
                                          Permission.EMPLOYEE_CHANGE_ROLE,
                                          Permission.EMPLOYEE_TOGGLE_ACTIVE,
                                          Permission.VACATION_APPROVE
                                      )),
            Employee.Role.EMPLOYEE,   EnumSet.noneOf(Permission.class)
    );

    public static Set<Permission> of(Employee.Role role) {
        return MAP.getOrDefault(role, EnumSet.noneOf(Permission.class));
    }
}

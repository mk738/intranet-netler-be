package com.company.intranet.security;

import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.common.response.ErrorResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final FirebaseAuth       firebaseAuth;
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper       objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest  request,
            HttpServletResponse response,
            FilterChain         chain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            FirebaseToken decoded = firebaseAuth.verifyIdToken(token);

            Optional<Employee> employeeOpt =
                    employeeRepository.findByFirebaseUid(decoded.getUid());

            if (employeeOpt.isEmpty()) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND,
                        ErrorCode.AUTH_ACCOUNT_NOT_FOUND,
                        "No account found for this Firebase user.");
                return;
            }

            Employee employee = employeeOpt.get();

            if (!employee.isActive()) {
                writeError(response, HttpServletResponse.SC_FORBIDDEN,
                        ErrorCode.AUTH_ACCOUNT_INACTIVE,
                        "This account has been deactivated.");
                return;
            }

            var auth = new UsernamePasswordAuthenticationToken(
                    employee,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name()))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (FirebaseAuthException e) {
            log.debug("Invalid Firebase token: {}", e.getMessage());
            // SecurityContext stays empty → Spring Security returns 401
        }

        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status,
                            ErrorCode code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(code.name(), message));
    }
}

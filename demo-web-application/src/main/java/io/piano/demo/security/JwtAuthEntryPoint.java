package io.piano.demo.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException e) throws IOException {
        final String errorMsg = e.getMessage();
        log.warn("Unauthorized. Message - {}", errorMsg);
        response.sendRedirect("apiError?error=" + errorMsg);
    }
}

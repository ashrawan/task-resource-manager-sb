package com.lk.taskmanager.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        final String expired = (String) httpServletRequest.getAttribute("expired");
        final String invalid = (String) httpServletRequest.getAttribute("invalid");
        if (expired != null) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, expired);
        } else if (invalid != null) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, invalid);
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}

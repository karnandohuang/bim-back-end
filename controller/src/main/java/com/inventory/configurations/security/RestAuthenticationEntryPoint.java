package com.inventory.configurations.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Component
public class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException, ServletException {
        httpServletResponse.setStatus(SC_FORBIDDEN);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message;
        if (e.getCause() != null) {
            message = e.getCause().getMessage();
        } else {
            message = e.getMessage();
        }
        byte[] body = new ObjectMapper()
                .writeValueAsBytes(Collections.singletonMap("error", message));
        httpServletResponse.getOutputStream().write(body);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("BIM_REALM");
        super.afterPropertiesSet();
    }
}

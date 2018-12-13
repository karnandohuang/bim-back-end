package com.inventory.configurations;

import com.inventory.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter implements Filter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Autowired
    private JwtService jwtService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        String header = servletRequest.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }


        UsernamePasswordAuthenticationToken token = getAuthentication(servletRequest);
        filterChain.doFilter(servletRequest, servletResponse);

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        token = token.replaceAll("Bearer ", "");
        // parse the token.
        String user = null;
        try {
            user = jwtService.verifyToken(token);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        if (user != null) {
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}

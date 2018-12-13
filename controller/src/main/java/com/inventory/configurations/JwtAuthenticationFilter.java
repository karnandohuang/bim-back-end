package com.inventory.configurations;

import com.inventory.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MemberDetailsService memberDetailsService;

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
                username = jwtService.verifyToken(authToken);
            } catch (IllegalArgumentException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = memberDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = this.getAuthentication(authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);
                if (authentication != null) {
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token, Authentication authentication,
                                                                  UserDetails userDetails) {
        // parse the token.
        String email = null;
        try {
            email = jwtService.verifyToken(token);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        if (email != null && authentication == null) {
            userDetails = memberDetailsService.loadUserByUsername(email);

            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }
        return null;
    }

    @Override
    public void destroy() {

    }
}

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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class CookieAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MemberDetailsService memberDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authToken = "";
        String email = "";

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 1) {
            for (Cookie ck : cookies) {
                if ("USERCOOKIE".equals(ck.getName())) {
                    authToken = ck.getValue();
                    System.out.println(authToken);
                    try {
                        email = jwtService.verifyToken(authToken);
                        System.out.println(email);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = null;
                        try {
                            userDetails = memberDetailsService.loadUserByUsername(email);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        UsernamePasswordAuthenticationToken authentication = this.getAuthentication(authToken, SecurityContextHolder.getContext().getAuthentication());
                        if (authentication != null) {
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        }

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                break;
            }
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token, Authentication authentication) {
        // parse the token.
        String email = null;
        try {
            email = jwtService.verifyToken(token);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        if (email != null && authentication == null) {
            UserDetails userDetails = memberDetailsService.loadUserByUsername(email);

            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }
        return null;
    }

    @Override
    public void destroy() {

    }
}

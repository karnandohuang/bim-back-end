package com.inventory.configurations.security;

import com.inventory.services.security.JwtService;
import com.inventory.services.security.MemberDetailsService;
import com.inventory.services.utils.exceptions.auth.JwtAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("com.inventory.services")
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MemberDetailsService memberDetailsService;

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken
                .class.equals(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String email;
        try {
            email = jwtService.verifyToken((String) authentication.getCredentials());
        } catch (RuntimeException e) {
            throw new JwtAuthenticationException(e.getMessage());
        }
        UserDetails user;
        try {
            user = memberDetailsService.loadUserByUsername(email);
        } catch (RuntimeException e) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        return new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
    }

}

package com.inventory.configurations.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@ComponentScan("com.inventory.configurations")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String ADMIN = "ADMIN";
    private static final String SUPERIOR = "SUPERIOR";
    private static final String EMPLOYEE = "EMPLOYEE";
    private static final String ALL_API_PATH = "/api/**";
    @Autowired
    private JwtAuthenticationProvider authenticationProvider;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers("/api/admins**").hasRole(ADMIN)
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/logout").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers(HttpMethod.GET, "/api/superiors").hasRole(SUPERIOR)
                .antMatchers(HttpMethod.POST, "/api/requests").hasAnyRole(EMPLOYEE, SUPERIOR)
                .antMatchers(HttpMethod.GET, "/api/requests/employee").hasAnyRole(EMPLOYEE, SUPERIOR)
                .antMatchers(HttpMethod.GET, "/api/requests/superior/employee").hasRole(SUPERIOR)
                .antMatchers(HttpMethod.PUT, "/api/requests/changeStatus").hasAnyRole(ADMIN, SUPERIOR)
                .antMatchers(HttpMethod.PUT, "/api/requests/**").hasRole(ADMIN)
                .antMatchers(HttpMethod.POST, ALL_API_PATH).hasRole(ADMIN)
                .antMatchers(HttpMethod.PUT, ALL_API_PATH).hasRole(ADMIN)
                .antMatchers(HttpMethod.DELETE, ALL_API_PATH).hasRole(ADMIN)
                .antMatchers(HttpMethod.GET).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
}

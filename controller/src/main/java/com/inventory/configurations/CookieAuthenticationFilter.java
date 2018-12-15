//package com.inventory.configurations;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class CookieAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//
//    protected CookieAuthenticationFilter(AuthenticationManager authenticationManager) {
//        super("/api/employees**"); //defaultFilterProcessesUrl - specified in applicationContext.xml.
//        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/employees**")); //Authentication will only be initiated for the request url matching this pattern
//        setAuthenticationManager(authenticationManager);
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
//
//        // get token from a Cookie
//        Cookie[] cookies = httpServletRequest.getCookies();
//
//        if( cookies == null || cookies.length < 1 ) {
//            throw new AuthenticationServiceException( "Invalid Token" );
//        }
//
//        Cookie userCookie = null;
//        for( Cookie cookie : cookies ) {
//            if( ( "someSessionId" ).equals( cookie.getName() ) ) {
//                userCookie = cookie;
//                break;
//            }
//        }
//
//        // TODO: move the cookie validation into a private method
//        if( userCookie == null || StringUtils.isEmpty( userCookie.getValue() ) ) {
//            throw new AuthenticationServiceException( "Invalid Token" );
//        }
//
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userCookie.getValue(), null, null);
//        return authToken;
//    }
//}

//package com.inventory.configurations;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.WebAttributes;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.util.HashMap;
//import java.util.Map;
//
//public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) {
//        UserContext userContext = (UserContext) authentication.getPrincipal();
//
//        JwtToken accessToken = tokenFactory.createAccessJwtToken(userContext);
//        JwtToken refreshToken = tokenFactory.createRefreshToken(userContext);
//
//        Map<String, String> tokenMap = new HashMap<String, String>();
//        tokenMap.put("token", accessToken.getToken());
//        tokenMap.put("refreshToken", refreshToken.getToken());
//
//        response.setStatus(HttpStatus.OK.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        mapper.writeValue(response.getWriter(), tokenMap);
//
//        clearAuthenticationAttributes(request);
//    }
//
//    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//
//        if (session == null) {
//            return;
//        }
//
//        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
//    }
//}

package com.inventory.services;

import com.inventory.services.security.JwtService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class JwtServiceTest {

    @InjectMocks
    JwtService jwtService;

    @Test
    public void generateTokenSuccess(){
        String email = "admin@gdn-commerce.com";
        String token = jwtService.generateToken(email);

        assertEquals(email, jwtService.verifyToken(token));

    }

    @Test
    public void generateTokenFailed(){
        String token = "abcde";

        try {
            jwtService.verifyToken(token);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}

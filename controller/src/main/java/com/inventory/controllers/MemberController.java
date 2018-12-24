package com.inventory.controllers;

import com.inventory.mappers.MemberHelper;
import com.inventory.services.member.MemberService;
import com.inventory.webmodels.requests.member.LoginRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.assignment.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.inventory.webmodels.API_PATH.API_PATH_LOGIN;

@CrossOrigin
@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberHelper helper;

    @PostMapping(value = API_PATH_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        BaseResponse<AuthenticationResponse> response;
        try {
            memberService.validateUser(request.getEmail(), request.getPassword());
            String token = memberService.authenticateUser(request.getEmail(), request.getPassword());
            String role = memberService.getMemberRole(request.getEmail());
            response = helper.getBaseResponse(true, "");
            response.setValue(helper.getMappedAuthenticationResponse(
                    request.getEmail(),
                    token, role));
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
            response.setValue(null);
        }
        return response;
    }
}

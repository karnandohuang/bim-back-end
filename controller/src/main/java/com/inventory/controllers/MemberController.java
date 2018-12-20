package com.inventory.controllers;

import com.inventory.mappers.MemberHelper;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.member.MemberService;
import com.inventory.webmodels.requests.member.LoginRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.CheckMemberResponse;
import com.inventory.webmodels.responses.assignment.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.inventory.webmodels.API_PATH.API_PATH_LOGIN;
import static java.util.stream.Collectors.toList;

@CrossOrigin
@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private MemberHelper helper;

    private UserDetails userDetails;

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

    @GetMapping(value = "/api/member/check")
    public BaseResponse<CheckMemberResponse> checkUserRole(@AuthenticationPrincipal Principal principal) {
        userDetails = memberService.getLoggedInUser(principal);
        BaseResponse<CheckMemberResponse> response = helper.getBaseResponse(true, "");
        CheckMemberResponse user = new CheckMemberResponse();
        user.setUsername(userDetails.getUsername());
        user.setRole(userDetails.getAuthorities().stream()
                .map(a -> (a).getAuthority()).collect(toList()));
        response.setValue(user);
        return response;
    }
}

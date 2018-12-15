package com.inventory.webmodels.responses.employee;

import lombok.Data;

import java.util.List;

@Data
public class CheckUserResponse {
    private String username;
    private List<String> role;
}

package com.inventory.webmodels.responses;

import lombok.Data;

import java.util.List;

@Data
public class CheckMemberResponse {
    private String username;
    private List<String> role;
}

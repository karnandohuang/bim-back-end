package com.inventory.webmodels.responses.admin;

import com.inventory.models.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminResponse {
    private Admin admin;
}

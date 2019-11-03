package com.inventory.webmodels.responses.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.models.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfAdminResponse {

    @JsonProperty("list")
    private List<Admin> value;
}

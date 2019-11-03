package com.inventory.controllers;

import com.inventory.helpers.AdminHelper;
import com.inventory.models.Paging;
import com.inventory.models.entity.Admin;
import com.inventory.services.admin.AdminService;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.services.utils.exceptions.admin.AdminNotFoundException;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.admin.AdminRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.admin.AdminResponse;
import com.inventory.webmodels.responses.admin.ListOfAdminResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.inventory.webmodels.API_PATH.API_PATH_ADMINS;
import static com.inventory.webmodels.API_PATH.API_PATH_GET_ADMIN;

@CrossOrigin
@RestController
public class AdminController {

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private AdminHelper helper;

    @Autowired
    private AdminService adminService;

    @GetMapping(value = API_PATH_ADMINS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfAdminResponse> listOfAdmin(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        ListOfAdminResponse list =
                new ListOfAdminResponse(adminService.getAdminList(paging));
        BaseResponse<ListOfAdminResponse> response = helper.getListBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_ADMIN, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<AdminResponse> getAdmin(
            @PathVariable String id) {
        BaseResponse<AdminResponse> response;
        try {
            Admin admin = null;
            if (id != null && !id.equals("null"))
                admin = adminService.getAdmin(id);
            response = helper.getBaseResponse(true, "");
            response.setValue(new AdminResponse(admin));
        } catch (AdminNotFoundException e) {
            response = helper.getBaseResponse(true, e.getMessage());
            response.setValue(null);
        }
        return response;
    }

    @RequestMapping(value = API_PATH_ADMINS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    @Transactional
    public BaseResponse<String> saveAdmin(@RequestBody AdminRequest request) {
        Admin admin = generalMapper.map(request, Admin.class);
        try {
            adminService.saveAdmin(admin);
            return helper.getStandardBaseResponse(true, "");
        } catch (RuntimeException e) {
            return helper.getStandardBaseResponse(false, e.getMessage());
        }
    }

    @DeleteMapping(value = API_PATH_ADMINS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<String> deleteAdmin(@RequestBody DeleteRequest request) {
        BaseResponse<String> response;
        try {
            String success = adminService.deleteAdmin(request.getIds());
            response = helper.getStandardBaseResponse(true, success);
        } catch (RuntimeException e) {
            response = helper.getStandardBaseResponse(false, e.getMessage());
        }
        return response;
    }
}

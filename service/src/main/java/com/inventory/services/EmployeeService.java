package com.inventory.services;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.webmodels.responses.*;

import javax.transaction.Transactional;
import java.util.List;

public interface EmployeeService {

    EmployeeResponse getEmployee(String id);

    StandardResponse login(String email, String password);

    ListOfSuperiorResponse getSuperiorList(Paging paging);

    ListOfEmployeeResponse getEmployeeList(Paging paging);

    Employee saveEmployee(Employee employee);

    DeleteResponse deleteEmployee(String[] ids);
}

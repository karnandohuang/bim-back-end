package com.inventory.services;

import com.inventory.models.Employee;
import com.inventory.models.Paging;

import java.util.List;

public interface EmployeeService {

    Employee getEmployee(String id);

    Employee login(String email, String password);

    List<Employee> getSuperiorList(Paging paging);

    List<Employee> getEmployeeList(Paging paging);

    Employee saveEmployee(Employee employee);

    List<String> deleteEmployee(List<String> ids);
}

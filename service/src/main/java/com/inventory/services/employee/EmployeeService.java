package com.inventory.services.employee;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.services.exceptions.employee.EmployeeNotFoundException;

import javax.transaction.Transactional;
import java.util.List;

public interface EmployeeService {

    Employee getEmployee(String id) throws EmployeeNotFoundException;

    Employee login(String email, String password);

    @Transactional
    List<Employee> getSuperiorList(String superiorId, String name, Paging paging) throws RuntimeException;

    @Transactional
    List<Employee> getEmployeeList(String name, Paging paging);

    Employee saveEmployee(Employee employee) throws RuntimeException;

    String deleteEmployee(List<String> ids) throws RuntimeException;
}

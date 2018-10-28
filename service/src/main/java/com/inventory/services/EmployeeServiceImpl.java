package com.inventory.services;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    @Transactional
    public Employee getEmployee(String id) {
        return employeeRepository.findById(id).get();
    }

    @Override
    public Employee login(String email, String password) {
        Employee employee;
        try {
            employee = employeeRepository.findByEmail(
                    email);
        } catch (Exception e) {
            return null;
        }
        if(encoder.matches(password, employee.getPassword()))
            return employeeRepository.save(employee);
        else
            return null;
    }


    @Override
    @Transactional
    public List<Employee> getSuperiorList(Paging paging) {
        List<Employee> list = employeeRepository.findAll();
        List<Employee> listOfSuperior = new ArrayList<>();
        for (Employee employee : list) {
            if (employee.getSuperiorId().equals("null")) {
                listOfSuperior.add(employee);
            }
        }
        return listOfSuperior;
    }

    @Override
    @Transactional
    public List<Employee> getEmployeeList(Paging paging) {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional
    public Employee saveEmployee(Employee employee) {
        employee.setPassword(encoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public List<String> deleteEmployee(List<String> ids) {
        List<String> listOfNotFoundIds = new ArrayList<>();
        for (String id : ids) {
            try {
                employeeRepository.deleteById(id);
            } catch (NullPointerException e) {
                listOfNotFoundIds.add("id " + id + " not found");
            }
        }
        return listOfNotFoundIds;
    }
}

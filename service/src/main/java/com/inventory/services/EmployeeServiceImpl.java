package com.inventory.services;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.repositories.EmployeeRepository;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmployeeResponse getEmployee(String id) {
       EmployeeResponse response =  new EmployeeResponse();
       response.setValue(employeeRepository.findById(id).get());
        return response;
    }

    @Override
    public StandardResponse login(String email, String password) {
        try {
            Employee employee = employeeRepository.findByEmailEqualsAndPasswordEquals(
                    email, password);
        }catch(Exception e){
            return new StandardResponse("false", "data is incorrect");
        }
        return new StandardResponse("true", "");
    }


    @Override
    @Transactional
    public ListOfSuperiorResponse getSuperiorList(Paging paging) {
        List<Employee> list = employeeRepository.findAll();
        List<Employee> listOfSuperior = new ArrayList<>();
        for (Employee employee:list) {
            if(employee.getSuperiorId().equals("null")){
                listOfSuperior.add(employee);
            }
        }
        ListOfSuperiorResponse response = new ListOfSuperiorResponse();
        response.setValue(listOfSuperior);
        return response;
    }

    @Override
    @Transactional
    public ListOfEmployeeResponse getEmployeeList(Paging paging) {
        ListOfEmployeeResponse list = new ListOfEmployeeResponse();
        list.setValue(employeeRepository.findAll());
        return list;
    }

    @Override
    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public DeleteResponse deleteEmployee(String[] ids) {
        List<StandardResponse> listOfResponse = new ArrayList<>();
        for (String id: ids) {
            try{
                employeeRepository.deleteById(id);
            }catch(NullPointerException e) {
                listOfResponse.add(new StandardResponse("false", "id " + id + " not found"));
            }
        }
                DeleteResponse response = new DeleteResponse("success", "");
                response.setValue(listOfResponse);
                return response;
    }
}

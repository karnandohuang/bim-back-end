package com.inventory.services.employee;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        if (encoder.matches(password, employee.getPassword()))
            return employeeRepository.save(employee);
        else
            return null;
    }

    @Override
    @Transactional
    public List<Employee> getEmployeeList(String name, Paging paging) {
        List<Employee> listOfEmployee;
        if (paging.getSortedType().matches("desc")) {
            listOfEmployee = employeeRepository.findAllByNameContainingIgnoreCase(name,
                    PageRequest.of(paging.getPageNumber() - 1,
                            paging.getPageSize(),
                            Sort.Direction.DESC,
                            paging.getSortedBy())).getContent();
        } else {
            listOfEmployee = employeeRepository.findAllByNameContainingIgnoreCase(name,
                    PageRequest.of(paging.getPageNumber() - 1,
                            paging.getPageSize(),
                            Sort.Direction.ASC,
                            paging.getSortedBy())).getContent();
        }
        float totalRecords = employeeRepository.countAllByNameContainingIgnoreCase(name);
        paging.setTotalRecords((int) totalRecords);
        double totalPage = (int) Math.ceil((totalRecords / paging.getPageSize()));
        paging.setTotalPage((int) totalPage);
        return listOfEmployee;
    }

    @Override
    @Transactional
    public List<Employee> getSuperiorList(String superiorId, String name, Paging paging) {
        if (name == null)
            name = "";
        if (superiorId == null)
            superiorId = "null";
        List<Employee> listOfEmployee;
        if (paging.getSortedType().matches("desc")) {
            listOfEmployee = employeeRepository.findAllBySuperiorIdAndNameContainingIgnoreCase(
                    superiorId, name, PageRequest.of(paging.getPageNumber() - 1,
                            paging.getPageSize(),
                            Sort.Direction.DESC,
                            paging.getSortedBy())).getContent();
        } else {
            listOfEmployee = employeeRepository.findAllBySuperiorIdAndNameContainingIgnoreCase(
                    superiorId, name, PageRequest.of(paging.getPageNumber() - 1,
                            paging.getPageSize(),
                            Sort.Direction.ASC,
                            paging.getSortedBy())).getContent();
        }
        float totalRecords = employeeRepository.countAllBySuperiorIdAndNameContainingIgnoreCase(
                superiorId, name);
        paging.setTotalRecords((int) totalRecords);
        double totalPage = (int) Math.ceil((totalRecords / paging.getPageSize()));
        paging.setTotalPage((int) totalPage);
        return listOfEmployee;
    }

    @Override
    @Transactional
    public Employee saveEmployee(Employee employee) {
        if (employee.getId() == null)
            employee.setPassword(encoder.encode(employee.getPassword()));
        if (employee.getSuperiorId().equals("null"))
            employee.setRole("SUPERIOR");
        else
            employee.setRole("EMPLOYEE");
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

package com.inventory.services.employee;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.repositories.EmployeeRepository;
import com.inventory.services.exceptions.EmployeeFieldWrongFormatException;
import com.inventory.services.exceptions.EmployeeNotFoundException;
import com.inventory.services.exceptions.EntityNullFieldException;
import com.inventory.services.validators.EntityValidator;
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

    @Autowired
    private EntityValidator validator;

    @Override
    @Transactional
    public Employee getEmployee(String id) throws EmployeeNotFoundException {
        if (!validator.validateIdFormatEntity(id, "EM"))
            throw new EmployeeNotFoundException("id not valid");
        try {
            return employeeRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new EmployeeNotFoundException("id not valid");
        }
    }

    @Override
    public Employee login(String email, String password) {
        boolean isEmailValid = validator.validateEmailFormatEmployee(email);
        if (!isEmailValid)
            return null;
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
    public List<Employee> getSuperiorList(String superiorId, String name, Paging paging)
            throws EmployeeFieldWrongFormatException {
        if (name == null)
            name = "";
        if (superiorId == null)
            superiorId = "null";
        if (!validator.validateIdFormatEntity(superiorId, "EM"))
            throw new EmployeeFieldWrongFormatException("Superior Id is not in the right format");
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
    public Employee saveEmployee(Employee employee) throws RuntimeException {
        employee.setPassword(encoder.encode(employee.getPassword()));
        employee.setRole(validator.assumeRoleEmployee(employee.getSuperiorId()));
        String nullFieldEmployee = validator.validateNullFieldEmployee(employee);
        boolean isSuperiorIdValid = validator.validateIdFormatEntity(employee.getSuperiorId(), "EM");
        boolean isEmailValid = validator.validateEmailFormatEmployee(employee.getEmail());
        if (nullFieldEmployee != null)
            throw new EntityNullFieldException(nullFieldEmployee);
        else if (!isEmailValid)
            throw new EmployeeFieldWrongFormatException("Email is not in the right format");
        else if (!isSuperiorIdValid)
            throw new EmployeeFieldWrongFormatException("Superior Id is not in the right format");
        else
            return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public List<String> deleteEmployee(List<String> ids) throws RuntimeException {
        List<String> listOfNotFoundIds = new ArrayList<>();
        for (String id : ids) {
            try {
                boolean isIdValid = validator.validateIdFormatEntity(id, "EM");
                if (!isIdValid)
                    throw new EmployeeFieldWrongFormatException("Id is not in the right format");
                employeeRepository.deleteById(id);
            } catch (RuntimeException e) {
                throw new EmployeeNotFoundException("id : " + id + " is not found");
            }
        }
        return listOfNotFoundIds;
    }
}

package com.inventory.services.employee;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.repositories.EmployeeRepository;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.exceptions.EntityNullFieldException;
import com.inventory.services.exceptions.employee.EmployeeAlreadyExistException;
import com.inventory.services.exceptions.employee.EmployeeFieldWrongFormatException;
import com.inventory.services.exceptions.employee.EmployeeNotFoundException;
import com.inventory.services.exceptions.employee.EmployeeStillHavePendingAssignmentException;
import com.inventory.services.validators.EmployeeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmployeeValidator validator;

    @Override
    @Transactional
    public Employee getEmployee(String id) throws EmployeeNotFoundException {
        if (!validator.validateIdFormatEntity(id, "EM"))
            throw new EmployeeFieldWrongFormatException("id is not in the right format");
        try {
            return employeeRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new EmployeeNotFoundException("id : " + id + " is not exist");
        }
    }

    @Override
    @Transactional
    public Employee getEmployeeByEmail(String email) throws EmployeeNotFoundException {
        if (!validator.validateEmailFormatEmployee(email))
            throw new EmployeeFieldWrongFormatException("email is not in the right format");
        try {
            return employeeRepository.findByEmail(email);
        } catch (RuntimeException e) {
            throw new EmployeeNotFoundException("employee of email : " + email + " is not exist");
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
        setPagingTotalRecordsAndTotalPage(paging, totalRecords);
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
        setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfEmployee;
    }

    private void setPagingTotalRecordsAndTotalPage(Paging paging, float totalRecords) {
        paging.setTotalRecords((int) totalRecords);
        double totalPage = (int) Math.ceil((totalRecords / paging.getPageSize()));
        paging.setTotalPage((int) totalPage);
    }

    private Employee editEmployee(Employee employee) {
        Employee newEmployee;
        try {
            newEmployee = employeeRepository.findById(employee.getId()).get();
        } catch (RuntimeException e) {
            throw new EmployeeNotFoundException("id : " + employee.getId() + " is not exist");
        }
        newEmployee.setName(employee.getName());
        newEmployee.setEmail(employee.getEmail());
        newEmployee.setDob(employee.getDob());
        newEmployee.setPosition(employee.getPosition());
        newEmployee.setDivision(employee.getDivision());
        newEmployee.setSuperiorId(employee.getSuperiorId());
        return newEmployee;
    }

    @Override
    @Transactional
    public Employee saveEmployee(Employee request) throws RuntimeException {

        String nullFieldEmployee = validator.validateNullFieldEmployee(request);

        Employee employee;

        if (request.getId() != null)

            employee = editEmployee(request);

        else {
            employee = request;

            employee.setPassword(encoder.encode(request.getPassword()));
        }

        Employee superior;

        if (employee.getSuperiorId().equals("null"))
            superior = new Employee();
        else
            superior = null;

        boolean isSuperiorIdValid = true;

        boolean isDobValid = validator.isDobValid(employee.getDob());

        boolean isEmailValid = validator.validateEmailFormatEmployee(employee.getEmail());

        try {
            superior = employeeRepository.findById(employee.getSuperiorId()).get();
        } catch (RuntimeException e) {
            //do nothing
        }

        employee.setRole(validator.assumeRoleEmployee(employee.getSuperiorId()));

        if (!employee.getSuperiorId().equals("null"))

            isSuperiorIdValid = validator.validateIdFormatEntity(employee.getSuperiorId(), "EM");

        if (nullFieldEmployee != null)
            throw new EntityNullFieldException(nullFieldEmployee);

        else if (!isEmailValid)
            throw new EmployeeFieldWrongFormatException("Email is not in the right format");

        else if (!isDobValid)
            throw new EmployeeFieldWrongFormatException("Date Of Birth is not in the right format");

        else if (!isSuperiorIdValid)
            throw new EmployeeFieldWrongFormatException("Superior Id is not in the right format");
        else if (employeeRepository.findByEmail(employee.getEmail()) != null)
            throw new EmployeeAlreadyExistException(employee.getEmail());

        else if (superior == null)
            throw new EmployeeNotFoundException("Superior of id : " + employee.getSuperiorId() + " is not exist");

        else
            return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public String deleteEmployee(List<String> ids) throws RuntimeException {
        for (String id : ids) {
                boolean isIdValid = validator.validateIdFormatEntity(id, "EM");
                if (!isIdValid)
                    throw new EmployeeFieldWrongFormatException("Id is not in the right format");
                else if (assignmentService.getAssignmentCountByEmployeeIdAndStatus(id, "Pending") > 0)
                    throw new EmployeeStillHavePendingAssignmentException();
                else {
                    try {
                        employeeRepository.findById(id).get();
                    } catch (RuntimeException e) {
                        throw new EmployeeNotFoundException("id : " + id + " is not exist");
                    }
                }
            employeeRepository.deleteById(id);
        }
        return "Delete success!";
    }
}

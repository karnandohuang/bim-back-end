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

import static com.inventory.services.ExceptionConstant.*;

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

    private final static String EMPLOYEE_ID_PREFIX = "EM";

    @Override
    @Transactional
    public Employee getEmployee(String id) throws EmployeeNotFoundException {
        if (!validator.validateIdFormatEntity(id, EMPLOYEE_ID_PREFIX))
            throw new EmployeeFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);
        try {
            return employeeRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new EmployeeNotFoundException(id, "Id");
        }
    }

    @Override
    @Transactional
    public Employee getEmployeeByEmail(String email) throws EmployeeNotFoundException {
        if (!validator.validateEmailFormatMember(email))
            throw new EmployeeFieldWrongFormatException(EMPLOYEE_EMAIL_WRONG_FORMAT_ERROR);
        try {
            return employeeRepository.findByEmail(email);
        } catch (RuntimeException e) {
            throw new EmployeeNotFoundException(email, "Email");
        }
    }

    @Override
    public Boolean login(String email, String password) {
        boolean isEmailValid = validator.validateEmailFormatMember(email);
        if (!isEmailValid)
            return false;
        Employee employee;
        try {
            employee = employeeRepository.findByEmail(
                    email);
        } catch (Exception e) {
            return false;
        }
        if (encoder.matches(password, employee.getPassword()))
            return true;
        else
            return false;
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
        if (superiorId.equals("all"))
            superiorId = "null";
        if (!validator.validateIdFormatEntity(superiorId, EMPLOYEE_ID_PREFIX) && !superiorId.equals("null"))
            throw new EmployeeFieldWrongFormatException(EMPLOYEE_SUPERIOR_ID_WRONG_FORMAT_ERROR);
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
            throw new EmployeeNotFoundException(employee.getId(), "Id");
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

        Employee isEmployeeExist = employeeRepository.findByEmail(employee.getEmail());

        Employee superior;

        if (employee.getSuperiorId().equals("null"))
            superior = new Employee();
        else {
            try {
                superior = employeeRepository.findById(employee.getSuperiorId()).get();
                superior.setRole(validator.assumeRoleEmployee(superior, true));
                employeeRepository.save(superior);
            } catch (RuntimeException e) {
                throw new EmployeeNotFoundException(employee.getSuperiorId(), "SuperiorId");
            }
        }
        boolean isSuperiorIdValid = true;

        boolean isDobValid = validator.isDobValid(employee.getDob());

        boolean isEmailValid = validator.validateEmailFormatMember(employee.getEmail());

        employee.setRole(validator.assumeRoleEmployee(employee, false));

        if (!employee.getSuperiorId().equals("null"))

            isSuperiorIdValid = validator.validateIdFormatEntity(employee.getSuperiorId(), EMPLOYEE_ID_PREFIX);

        if (nullFieldEmployee != null)
            throw new EntityNullFieldException(nullFieldEmployee);

        else if (!isEmailValid)
            throw new EmployeeFieldWrongFormatException(EMPLOYEE_EMAIL_WRONG_FORMAT_ERROR);

        else if (!isDobValid)
            throw new EmployeeFieldWrongFormatException(EMPLOYEE_DOB_WRONG_FORMAT_ERROR);

        else if (!isSuperiorIdValid)
            throw new EmployeeFieldWrongFormatException(EMPLOYEE_SUPERIOR_ID_WRONG_FORMAT_ERROR);
        else if (isEmployeeExist != null && !isEmployeeExist.getId().equals(employee.getId()))
            throw new EmployeeAlreadyExistException(employee.getEmail());
        else if (superior == null)
            throw new EmployeeNotFoundException(employee.getSuperiorId(), "SuperiorId");
        else
            return employeeRepository.save(employee);
    }

    private boolean isEmployeeHavingSubordinate(String id) {
//        this.getEmployee(id);
        Float count = employeeRepository.countAllBySuperiorIdAndNameContainingIgnoreCase(id, "");
        if (count > 1)
            return true;
        else
            return false;
    }

    @Override
    @Transactional
    public String deleteEmployee(List<String> ids) throws RuntimeException {
        for (String id : ids) {
            Employee employee = employeeRepository.findById(id).get();
            if (assignmentService.getAssignmentCountByEmployeeId(id).get("pendingAssignmentCount") > 0)
                    throw new EmployeeStillHavePendingAssignmentException();
                else {
                    try {
                        if (!this.isEmployeeHavingSubordinate(employee.getSuperiorId()) &&
                                !employee.getSuperiorId().equals("null")) {
                            Employee superior = employeeRepository.findById(employee.getSuperiorId()).get();
                            superior.setRole(validator.assumeRoleEmployee(superior, false));
                            employeeRepository.save(superior);
                        } else if (employeeRepository.countAllBySuperiorIdAndNameContainingIgnoreCase(id, "") > 0) {
                            List<Employee> listOfEmployee = employeeRepository.findAllBySuperiorId(id);
                            for (Employee e : listOfEmployee) {
                                e.setSuperiorId("null");
                                employeeRepository.save(e);
                            }
                        }
                    } catch (RuntimeException e) {
                        throw new EmployeeNotFoundException(id, "Id");
                    }
                }
            employeeRepository.deleteById(id);
        }
        return "Delete success!";
    }
}

package com.inventory.services.employee;

import com.inventory.models.Paging;
import com.inventory.models.entity.Employee;
import com.inventory.repositories.EmployeeRepository;
import com.inventory.services.GeneralMapper;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.exceptions.EntityNullFieldException;
import com.inventory.services.exceptions.employee.EmployeeAlreadyExistException;
import com.inventory.services.exceptions.employee.EmployeeFieldWrongFormatException;
import com.inventory.services.exceptions.employee.EmployeeNotFoundException;
import com.inventory.services.exceptions.employee.EmployeeStillHavePendingAssignmentException;
import com.inventory.services.helper.PagingHelper;
import com.inventory.services.validators.EmployeeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.inventory.services.constants.ExceptionConstant.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private GeneralMapper mapper;

    @Autowired
    private EmployeeValidator validator;

    @Autowired
    private PagingHelper pagingHelper;

    private final static String EMPLOYEE_ID_PREFIX = "EM";
    private final static Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Override
    @Transactional
    public Employee getEmployee(String id) throws EmployeeNotFoundException {
        if (!validator.validateIdFormatEntity(id, EMPLOYEE_ID_PREFIX)) {
            logger.info("employee id : " + id + " is in the wrong format!");
            throw new EmployeeFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);
        }
        try {
            return employeeRepository.findById(id).get();
        } catch (RuntimeException e) {
            logger.info("error getting employee of id : " + id);
            throw new EmployeeNotFoundException(id, "Id");
        }
    }

    @Override
    @Transactional
    public Employee getEmployeeByEmail(String email) throws EmployeeNotFoundException {
        if (!validator.validateEmailFormatMember(email)) {
            logger.info("email passed is in the wrong format!");
            throw new EmployeeFieldWrongFormatException(MEMBER_EMAIL_WRONG_FORMAT_ERROR);
        }
        Employee employee = null;
        employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            logger.info("error getting employee of email : " + email);
            throw new EmployeeNotFoundException(email, "Email");
        }
        return employee;
    }

    @Override
    public Boolean login(String email, String password) {
        Employee employee = null;
        employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            logger.info("email : " + email + " is wrong. not listed on the database!");
            return false;
        }
        if (!encoder.matches(password, employee.getPassword())) {
            logger.info("password is wrong!");
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public List<Employee> getEmployeeList(String name, Paging paging) {
        List<Employee> listOfEmployee;
        PageRequest pageRequest;
        if (paging.getSortedType().matches("desc")) {
            pageRequest = PageRequest.of(
                    paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.DESC,
                    paging.getSortedBy());
        } else {
            pageRequest = PageRequest.of(
                    paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.ASC,
                    paging.getSortedBy());
        }
        listOfEmployee = employeeRepository.findAllByNameContainingIgnoreCase(name, pageRequest).getContent();
        float totalRecords = employeeRepository.countAllByNameContainingIgnoreCase(name);
        pagingHelper.setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfEmployee;
    }

    @Override
    @Transactional
    public List<Employee> getSuperiorList(String superiorId, String name, Paging paging)
            throws EmployeeFieldWrongFormatException {
        if (name == null)
            name = "";
        else if (superiorId == null)
            superiorId = "-";
        else if (!validator.validateIdFormatEntity(superiorId, EMPLOYEE_ID_PREFIX) && !superiorId.equals("-")) {
            logger.info("superior id format is wrong!");
            throw new EmployeeFieldWrongFormatException(EMPLOYEE_SUPERIOR_ID_WRONG_FORMAT_ERROR);
        }
        List<Employee> listOfEmployee;
        PageRequest pageRequest;
        if (paging.getSortedType().matches("desc")) {
            pageRequest = PageRequest.of(
                    paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.DESC,
                    paging.getSortedBy());
        } else {
            pageRequest = PageRequest.of(
                    paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.ASC,
                    paging.getSortedBy());
        }
        listOfEmployee = employeeRepository.findAllBySuperiorIdAndNameContainingIgnoreCase(
                superiorId, name, pageRequest).getContent();
        float totalRecords = employeeRepository.countAllBySuperiorIdAndNameContainingIgnoreCase(
                superiorId, name);
        pagingHelper.setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfEmployee;
    }

    private Employee editEmployee(Employee request) {
        String password = this.getEmployee(request.getId()).getPassword();
        if (request.getPassword() != null)
            password = encoder.encode(request.getPassword());
        Employee e = mapper.map(request, Employee.class);
        e.setPassword(password);
        return e;
    }

    private boolean isEmployeeHavingSubordinate(String id) {
        Float count = employeeRepository.countAllBySuperiorIdAndNameContainingIgnoreCase(id, "");
        logger.info("employee count for superior : " + id + " is " + count);
        if (count > 1)
            return true;
        return false;
    }

    @Override
    @Transactional
    public Employee saveEmployee(Employee request) throws RuntimeException {

        String nullFieldEmployee = validator.validateNullFieldEmployee(request);

        Employee employee;

        if (request.getId() != null) {
            employee = this.getEmployee(request.getId());
            if (!this.isEmployeeHavingSubordinate(employee.getSuperiorId()) &&
                    !employee.getSuperiorId().equals("-")) {
                Employee superior = employeeRepository.findById(employee.getSuperiorId()).get();
                superior.setRole(validator.assumeRoleEmployee(superior, false));
                employeeRepository.save(superior);
                logger.info("Superior : " + employee.getSuperiorId() + " changed to employee role");
            }
            employee = editEmployee(request);
        }

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
            throw new EmployeeFieldWrongFormatException(MEMBER_EMAIL_WRONG_FORMAT_ERROR);

        else if (!isDobValid)
            throw new EmployeeFieldWrongFormatException(EMPLOYEE_DOB_WRONG_FORMAT_ERROR);

        else if (!isSuperiorIdValid)
            throw new EmployeeFieldWrongFormatException(EMPLOYEE_SUPERIOR_ID_WRONG_FORMAT_ERROR);

        else if (isEmployeeExist != null && !isEmployeeExist.getId().equals(employee.getId()))
            throw new EmployeeAlreadyExistException(employee.getEmail());

        else {
            if (employee.getSuperiorId().equals("null")) {
                logger.info("Set superior id to -");
                employee.setSuperiorId("-");
            }
            return employeeRepository.save(employee);
        }
    }

    @Override
    @Transactional
    public String deleteEmployee(List<String> ids) throws RuntimeException {
        for (String id : ids) {
            Employee employee = this.getEmployee(id);
            if (assignmentService.getAssignmentCountByEmployeeId(id).get("pendingAssignmentCount") > 0)
                    throw new EmployeeStillHavePendingAssignmentException();
                else {
                    try {
                        if (!this.isEmployeeHavingSubordinate(employee.getSuperiorId()) &&
                                !employee.getSuperiorId().equals("-")) {
                            Employee superior = employeeRepository.findById(employee.getSuperiorId()).get();
                            superior.setRole(validator.assumeRoleEmployee(superior, false));
                            employeeRepository.save(superior);
                            logger.info("Superior : " + employee.getSuperiorId() + " changed to employee role");
                        } else if (employeeRepository.countAllBySuperiorIdAndNameContainingIgnoreCase(id, "") > 0) {
                            List<Employee> listOfEmployee = employeeRepository.findAllBySuperiorId(id);
                            for (Employee e : listOfEmployee) {
                                e.setSuperiorId("-");
                                employeeRepository.save(e);
                            }
                            logger.info("Employee with this superior is changed to null superior");
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

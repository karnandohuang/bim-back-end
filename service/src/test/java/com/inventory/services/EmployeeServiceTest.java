package com.inventory.services;

import com.inventory.models.Paging;
import com.inventory.models.entity.Employee;
import com.inventory.repositories.EmployeeRepository;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.employee.EmployeeServiceImpl;
import com.inventory.services.helper.PagingHelper;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.services.utils.validators.EmployeeValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeValidator validator;

    @Mock
    private PagingHelper pagingHelper;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private GeneralMapper mapper;

    private Paging paging = new Paging();
    private List<Employee> employeeList = mock(ArrayList.class);
    private Page<Employee> employeePageList = mock(Page.class);

    @Test
    public void getEmployeeIdValidSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        String id = "EM040";
        mockValidateId(true, id);
        mockFindEmployeeById(true, id, employee);
        Employee e = employeeService.getEmployee(id);
        assertEquals(employee, e);
        verify(validator).validateIdFormatEntity(id, "EM");
        verify(employeeRepository).findById(id);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    public void getEmployeeIdValidFailed() {
        String id = "EM099";
        mockValidateId(true, id);
        mockFindEmployeeById(false, id, null);
        try {
            employeeService.getEmployee(id);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(id, "EM");
            verify(employeeRepository).findById(id);
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Test
    public void getEmployeeIdNotValidFailed() {
        mockValidateId(false, "99");
        try {
            employeeService.getEmployee("99");
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity("99", "EM");
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(employeeRepository);
        }
    }

    @Test
    public void getEmployeeEmailValidSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        mockValidateEmail(true, employee.getEmail());
        mockFindEmployeeByEmail(true, employee.getEmail(), employee);
        try {
            Employee e = employeeService.getEmployeeByEmail(employee.getEmail());
            assertEquals(employee, e);
        } catch (RuntimeException e) {
            verify(employeeRepository).findByEmail(employee.getEmail());
        }
    }

    @Test
    public void getEmployeeEmailValidNotFoundFailed() {
        String email = "asd@gdn-commerce.com";
        mockValidateEmail(true, email);
        mockFindEmployeeByEmail(false, email, null);
        try {
            employeeService.getEmployeeByEmail(email);
        } catch (RuntimeException e) {
            verify(employeeRepository).findByEmail(email);
        }
    }

    @Test
    public void getEmployeeEmailNotValidFailed() {
        String email = "stelli@gn-commerce.com";
        mockValidateEmail(false, email);
        try {
            employeeService.getEmployeeByEmail(email);
        } catch (RuntimeException e) {
            verify(validator).validateEmailFormatMember(anyString());
            verifyZeroInteractions(employeeRepository);
            verifyNoMoreInteractions(validator);
        }
    }

    @Test
    public void loginEmployeeEmailValidAndPasswordMatchSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        mockValidateEmail(true, employee.getEmail());
        mockFindEmployeeByEmail(true, employee.getEmail(), employee);
        mockBcryptEncoderMatch(true);
        assertTrue(employeeService.login(employee.getEmail(), employee.getPassword()));
        verify(employeeRepository).findByEmail(employee.getEmail());
    }

    @Test
    public void loginEmployeeEmailValidAndPasswordNotMatchFailed() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        String passsword = "wrongPassword";
        mockValidateEmail(true, employee.getEmail());
        mockFindEmployeeByEmail(true, employee.getEmail(), employee);
        mockBcryptEncoderMatch(false);
        assertFalse(employeeService.login(employee.getEmail(), passsword));
        verify(employeeRepository).findByEmail(employee.getEmail());
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    public void loginEmployeeEmailValidNotFoundFailed() {
        String email = "karnandoa@gdn-commerce.com";
        mockValidateEmail(true, email);
        mockFindEmployeeByEmail(false, email, null);
        try {
            employeeService.login(email, "karnando");
        } catch (RuntimeException e) {
            verify(employeeRepository).findByEmail("karnandoa@gdn-commerce.com");
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Test
    public void findEmployeeBasedOnNameAndPagingDescSuccess() {
        mockEmployeeListBasedOnNameAndPaging();
        setPaging("desc");
        List<Employee> returnEmployees = employeeService.getEmployeeList("", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository).findAllByNameContainingIgnoreCase(any(String.class),
                pageArgument.capture());
        verify(employeeRepository).countAllByNameContainingIgnoreCase(anyString());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(employeeList.size(), returnEmployees.size());
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    public void findEmployeeBasedOnNameAndPagingAscSuccess() {
        mockEmployeeListBasedOnNameAndPaging();
        setPaging("asc");
        List<Employee> returnEmployees = employeeService.getEmployeeList("", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository, times(1)).findAllByNameContainingIgnoreCase(any(String.class),
                pageArgument.capture());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor("updatedDate").getDirection());

        assertEquals(employeeList.size(), returnEmployees.size());
    }

    @Test
    public void findEmployeeBasedOnNameNullAndPagingAscSuccess() {
        mockEmployeeListBasedOnNameAndPaging();
        setPaging("asc");
        List<Employee> returnEmployees = employeeService.getEmployeeList(null, paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository, times(1)).findAllByNameContainingIgnoreCase(any(String.class),
                pageArgument.capture());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor("updatedDate").getDirection());

        assertEquals(employeeList.size(), returnEmployees.size());
    }

    @Test
    public void findSuperiorBasedOnSuperiorIdNameAndPagingDescSuccess() {
        mockValidateId(true, "EM040");
        mockSuperiorListBasedOnSuperiorIdNameAndPaging();
        setPaging("desc");
        List<Employee> returnEmployees = employeeService.getSuperiorList("EM040", "", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository)
                .findAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString(), any(Pageable.class));
        verify(employeeRepository)
                .countAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString());
        verify(employeeRepository, times(1))
                .findAllBySuperiorIdAndNameContainingIgnoreCase(any(String.class), any(String.class),
                        pageArgument.capture());
        verify(validator).validateIdFormatEntity(anyString(), anyString());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor("updatedDate").getDirection());

        assertEquals(employeeList.size(), returnEmployees.size());
        verifyNoMoreInteractions(employeeRepository);
        verifyNoMoreInteractions(validator);
    }

    @Test
    public void findSuperiorBasedOnSuperiorIdAndNameNullAndPagingDescSuccess() {
        mockValidateId(true, "EM040");
        mockSuperiorListBasedOnSuperiorIdNameAndPaging();
        setPaging("desc");
        List<Employee> returnEmployees = employeeService.getSuperiorList("EM040", null, paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository)
                .findAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString(), any(Pageable.class));
        verify(employeeRepository)
                .countAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString());
        verify(employeeRepository, times(1))
                .findAllBySuperiorIdAndNameContainingIgnoreCase(any(String.class), any(String.class),
                        pageArgument.capture());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor("updatedDate").getDirection());

        assertEquals(employeeList.size(), returnEmployees.size());
        verifyNoMoreInteractions(employeeRepository);
        verifyNoMoreInteractions(validator);
    }

    @Test
    public void findSuperiorBasedOnSuperiorIdNullAndNameAndPagingDescSuccess() {
        mockSuperiorListBasedOnSuperiorIdNameAndPaging();
        setPaging("desc");
        List<Employee> returnEmployees = employeeService.getSuperiorList(null, "", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository, times(1))
                .findAllBySuperiorIdAndNameContainingIgnoreCase(any(String.class), any(String.class),
                        pageArgument.capture());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor("updatedDate").getDirection());

        assertEquals(employeeList.size(), returnEmployees.size());
    }

    @Test
    public void findSuperiorBasedOnSuperiorIdNameAndPagingAscSuccess() {
        mockValidateId(true, "EM040");
        mockSuperiorListBasedOnSuperiorIdNameAndPaging();
        setPaging("asc");
        List<Employee> returnEmployees = employeeService.getSuperiorList("EM040", "", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository, times(1))
                .findAllBySuperiorIdAndNameContainingIgnoreCase(any(String.class), any(String.class),
                        pageArgument.capture());


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor("updatedDate").getDirection());

        assertEquals(employeeList.size(), returnEmployees.size());
    }

    @Test
    public void findSuperiorBasedOnSuperiorIdNotValidAndNameAndPagingDescFailed() {
        mockValidateId(false, "40");
        mockSuperiorListBasedOnSuperiorIdNameAndPaging();
        setPaging("desc");
        try {
            List<Employee> returnEmployees = employeeService.getSuperiorList("40", "", paging);
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void insertEmployeeSuperiorIdNullSuccess() {
        Employee employee = setEmployeeWithIdNullAndSuperiorIdNull();
        mockValidateEmail(true, employee.getEmail());
        mockValidateDOB(true, employee.getDob());
        mockSaveEmployee(employee);
        Employee returned = employeeService.saveEmployee(employee);
        ArgumentCaptor<Employee> employeeArgument = ArgumentCaptor.forClass(Employee.class);
        verify(validator).validateNullFieldEmployee(employee);
        verify(validator).validateEmailFormatMember(employee.getEmail());
        verify(validator).validateDobFormatEmployee(employee.getDob());
        verify(validator).assumeRoleEmployee(employee, false);
        verify(employeeRepository).findByEmail(employee.getEmail());
        verify(employeeRepository).save(employeeArgument.capture());

        assertEquals(employee, employeeArgument.getValue());
        assertEquals(employee, returned);

        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    public void insertEmployeeSuperiorIdValidSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        setsuperiorIdWithinEmployee(employee);
        Employee superior = setSuperiorWithIdAndSuperiorIdNull(employee.getSuperiorId());
        mockValidateEmail(true, employee.getEmail());
        mockValidateDOB(true, employee.getDob());
        mockValidateId(true, employee.getSuperiorId());
        mockValidateId(true, employee.getId());
        mockFindEmployeeById(true, employee.getId(), employee);
        mockFindEmployeeById(true, superior.getId(), superior);
        mockMapEmployee(false, employee);
        mockSaveEmployee(employee);
        Employee returned = employeeService.saveEmployee(employee);
        ArgumentCaptor<Employee> employeeArgument = ArgumentCaptor.forClass(Employee.class);
        verify(validator).validateNullFieldEmployee(employee);
        verify(validator).validateEmailFormatMember(employee.getEmail());
        verify(validator).validateDobFormatEmployee(employee.getDob());
        verify(employeeRepository).findById(anyString());
        verify(validator).validateIdFormatEntity(employee.getId(), "EM");
        verify(validator).validateIdFormatEntity(employee.getSuperiorId(), "EM");
        verify(validator, times(3)).assumeRoleEmployee(any(Employee.class), anyBoolean());
        verify(employeeRepository).findByEmail(employee.getEmail());
        verify(employeeRepository, times(2)).findById(employee.getSuperiorId());
        verify(employeeRepository, times(3)).save(employeeArgument.capture());

        assertEquals(employee, employeeArgument.getValue());
        assertEquals(employee, returned);

        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    public void insertEmployeeSuperiorIdValidNotFoundFailed() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        employee.setSuperiorId("EM099");
        employee.setId(null);
        mockFindEmployeeById(false, employee.getSuperiorId(), null);
        mockValidateId(true, employee.getSuperiorId());
        mockValidateEmail(true, employee.getEmail());
        mockValidateDOB(true, employee.getDob());
        System.out.println(employee.getId());
        mockSaveEmployee(employee);
        try {
            Employee returned = employeeService.saveEmployee(employee);
        } catch (RuntimeException e) {
            verify(employeeRepository).findById(employee.getSuperiorId());
            verify(employeeRepository).findByEmail(employee.getEmail());

            verify(validator).validateNullFieldEmployee(employee);
            verify(validator).validateIdFormatEntity(employee.getSuperiorId(), "EM");

            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Test
    public void insertEmployeeSuperiorIdValidEmailNullFieldExistsFailed() {
        Employee employee = setEmployeeWithIdNullAndSuperiorIdNullAndEmailNull();
        mockValidateId(true, employee.getId());
        mockNullFieldEmployeeFound(true);
        mockSaveEmployee(employee);
        try {
            Employee returned = employeeService.saveEmployee(employee);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldEmployee(employee);
            verify(validator).validateEmailFormatMember(employee.getEmail());
            verify(validator).validateDobFormatEmployee(employee.getDob());
            verify(validator).assumeRoleEmployee(any(Employee.class), anyBoolean());
            verify(employeeRepository).findByEmail(employee.getEmail());

            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Test
    public void insertEmployeeSuperiorIdValidEmailNotValidFailed() {
        Employee employee = setEmployeeWithIdNullAndSuperiorIdNullAndEmailNull();
        employee.setEmail("stelli@gn-commerce.com");
        mockValidateEmail(false, employee.getEmail());
        mockSaveEmployee(employee);
        try {
            Employee returned = employeeService.saveEmployee(employee);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldEmployee(employee);
            verify(validator).validateDobFormatEmployee(employee.getDob());
            verify(validator).validateEmailFormatMember(employee.getEmail());
            verify(validator).assumeRoleEmployee(any(Employee.class), anyBoolean());
            verify(employeeRepository).findByEmail(employee.getEmail());

            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Test
    public void insertEmployeeSuperiorIdValidDobNotValid() {
        Employee employee = setEmployeeWithIdNullAndSuperiorIdNull();
        Employee superior = setSuperiorWithIdAndSuperiorIdNull(employee.getSuperiorId());
        employee.setDob("aaaaaa");
        mockFindEmployeeById(true, employee.getSuperiorId(), superior);
        mockValidateEmail(true, employee.getEmail());
        mockValidateDOB(false, employee.getDob());
        mockSaveEmployee(employee);
        try {
            Employee returned = employeeService.saveEmployee(employee);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldEmployee(employee);
            verify(validator).validateEmailFormatMember(employee.getEmail());
            verify(validator).validateDobFormatEmployee(employee.getDob());
            verify(validator).assumeRoleEmployee(employee, false);
            verify(employeeRepository).findByEmail(employee.getEmail());
            verifyNoMoreInteractions(employeeRepository);
            verifyNoMoreInteractions(validator);
        }
    }

    @Test
    public void insertEmployeeSuperiorIdNotValid() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        employee.setSuperiorId("99");
        mockValidateId(false, employee.getSuperiorId());
        mockValidateEmail(true, employee.getEmail());
        mockSaveEmployee(employee);
        try {
            employeeService.saveEmployee(employee);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldEmployee(employee);
            verify(validator).validateIdFormatEntity(employee.getId(), "EM");
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Test
    public void editEmployeeSuperiorIdNullAndPasswordNullSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        mockValidateId(true, employee.getId());
        mockValidateId(true, employee.getSuperiorId());
        mockValidateEmail(true, employee.getEmail());
        mockValidateDOB(true, employee.getDob());
        mockFindEmployeeById(true, employee.getId(), employee);
        mockMapEmployee(false, employee);
        employee.setSuperiorId("-");
        employee.setPassword(null);
        mockSaveEmployee(employee);
        Employee returned = employeeService.saveEmployee(employee);
    }

    @Test
    public void editEmployeeSuperiorIdNullAndPasswordSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        mockValidateId(true, employee.getId());
        mockValidateId(true, employee.getSuperiorId());
        mockValidateEmail(true, employee.getEmail());
        mockValidateDOB(true, employee.getDob());
        mockFindEmployeeById(true, employee.getId(), employee);
        employee.setPassword("asd75");
        mockMapEmployee(false, employee);
        mockSaveEmployee(employee);
        Employee returned = employeeService.saveEmployee(employee);
    }

    @Test
    public void editEmployeeSuperiorIdValidAndPasswordNullSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        employee.setSuperiorId("EM038");
        Employee superior = setSuperiorWithIdAndSuperiorIdNull("EM038");
        mockValidateId(true, employee.getId());
        mockValidateId(true, employee.getSuperiorId());
        mockFindEmployeeById(true, employee.getSuperiorId(), superior);
        mockValidateEmail(true, employee.getEmail());
        mockValidateDOB(true, employee.getDob());
        mockFindEmployeeById(true, employee.getId(), employee);
        mockMapEmployee(false, employee);
        employee.setSuperiorId("EM036");
        employee.setPassword(null);
        superior = setSuperiorWithIdAndSuperiorIdNull("EM036");
        mockValidateId(true, employee.getSuperiorId());
        mockFindEmployeeById(true, employee.getSuperiorId(), superior);
        mockSaveEmployee(employee);
        System.out.println(employee.getSuperiorId());
        Employee returned = employeeService.saveEmployee(employee);
        assertEquals(employee, returned);
    }

    @Test
    public void editEmployeeSuperiorIdExistAndSuperiorIdValidAndPasswordNullSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        employee.setSuperiorId("EM038");
        Employee superior = setSuperiorWithIdAndSuperiorIdNull("EM038");
        mockValidateId(true, employee.getId());
        mockValidateId(true, employee.getSuperiorId());
        mockFindEmployeeById(true, employee.getSuperiorId(), superior);
        mockValidateEmail(true, employee.getEmail());
        mockValidateDOB(true, employee.getDob());
        mockFindEmployeeByIdWithSuperiorId(true, employee.getId(), employee.getSuperiorId());
        mockMapEmployee(false, employee);
        employee.setSuperiorId("EM036");
        employee.setPassword(null);
        superior = setSuperiorWithIdAndSuperiorIdNull("EM036");
        mockValidateId(true, employee.getSuperiorId());
        mockFindEmployeeById(true, employee.getSuperiorId(), superior);
        mockSaveEmployee(employee);
        System.out.println(employee.getSuperiorId());
        Employee returned = employeeService.saveEmployee(employee);
        assertEquals(employee, returned);
    }

    @Test
    public void editEmployeeSameEmployeeIdAndSuperiorIdFailed() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        employee.setSuperiorId(employee.getId());
        mockSaveEmployee(employee);
        try {
            employeeService.saveEmployee(employee);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldEmployee(employee);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Test
    public void editEmployeeEmailAlreadyTakenFailed() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        mockNullFieldEmployeeFound(false);
        mockMapEmployee(false, employee);
        employee.setEmail("abc@gdn-commerce.com");
        mockValidateId(true, employee.getId());
        mockValidateDOB(true, employee.getDob());
        mockFindEmployeeById(true, employee.getId(), employee);
        mockValidateEmail(true, employee.getEmail());
        mockEmployeeEmailAlreadyTaken(employee.getEmail());
        mockSaveEmployee(employee);
        try {
            employeeService.saveEmployee(employee);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            verify(employeeRepository).countAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString());
            verify(employeeRepository).findById(anyString());
            verify(employeeRepository).findByEmail(anyString());
            verify(validator).validateIdFormatEntity(employee.getId(), "EM");
            verify(validator).validateDobFormatEmployee(employee.getDob());
            verify(validator).validateEmailFormatMember(employee.getEmail());
            verify(validator).assumeRoleEmployee(any(Employee.class), anyBoolean());
            verify(validator).validateNullFieldEmployee(employee);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(employeeRepository);
        }
    }

    @Test
    public void deleteEmployeeByListOfIdSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        List<String> ids = new ArrayList<>();
        ids.add(employee.getId());
        mockValidateId(true, ids.get(0));
        mockFindEmployeeById(true, ids.get(0), employee);
        mockEmployeeFindSubordinateCount(false, ids.get(0));
        mockEmployeeFoundPendingAssignment(false, ids.get(0));
        assertEquals("Delete success!", employeeService.deleteEmployee(ids));
        verify(employeeRepository).findById(ids.get(0));
        verify(employeeRepository).countAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString());
        verify(employeeRepository).deleteById(ids.get(0));
        verify(validator).validateIdFormatEntity(ids.get(0), "EM");
        verify(assignmentService).getAssignmentCountByEmployeeId(ids.get(0));
        verifyNoMoreInteractions(employeeRepository);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(assignmentService);
    }

    @Test
    public void deleteEmployeeByListOfIdHaveOneSubordinateSuccess() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        List<String> ids = new ArrayList<>();
        ids.add(employee.getId());
        mockValidateId(true, ids.get(0));
        mockFindEmployeeById(true, ids.get(0), employee);
        mockEmployeeFindSubordinateCount(true, ids.get(0));
        mockListOfEmployeeSubordinate(true, ids.get(0));
        mockEmployeeFoundPendingAssignment(false, ids.get(0));
        assertEquals("Delete success!", employeeService.deleteEmployee(ids));
        verify(employeeRepository).findById(ids.get(0));
        verify(employeeRepository).findAllBySuperiorId(ids.get(0));
        verify(employeeRepository).save(any(Employee.class));
        verify(employeeRepository).countAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString());
        verify(employeeRepository).deleteById(ids.get(0));
        verify(validator).validateIdFormatEntity(ids.get(0), "EM");
        verify(assignmentService).getAssignmentCountByEmployeeId(ids.get(0));
        verifyNoMoreInteractions(employeeRepository);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(assignmentService);
    }

    @Test
    public void deleteEmployeeByListOfIdHavePendingAssignmentFailed() {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        List<String> ids = new ArrayList<>();
        ids.add(employee.getId());
        mockValidateId(true, ids.get(0));
        mockFindEmployeeById(true, ids.get(0), employee);
        mockEmployeeFoundPendingAssignment(true, ids.get(0));
        try {
            employeeService.deleteEmployee(ids);
        } catch (RuntimeException e) {
            verify(employeeRepository).findById(ids.get(0));
            verify(validator).validateIdFormatEntity(ids.get(0), "EM");
            verify(assignmentService).getAssignmentCountByEmployeeId(ids.get(0));
            verifyNoMoreInteractions(employeeRepository);
            verifyZeroInteractions(validator);
            verifyNoMoreInteractions(assignmentService);
        }
    }

    @Test
    public void deleteEmployeeByListOfIdSuperiorIdHaveOneSubordinateSuccess() {
        String superiorId = "EM036";
        List<String> ids = new ArrayList<>();
        ids.add("EM040");
        mockValidateId(true, ids.get(0));
        mockFindEmployeeByIdWithSuperiorId(true, "EM040", superiorId);
        mockEmployeeFoundPendingAssignment(false, ids.get(0));
        mockEmployeeFindSubordinateCount(true, superiorId);
        Employee superior = setSuperiorWithIdAndSuperiorIdNull(superiorId);
        mockFindEmployeeById(true, superiorId, superior);
        assertEquals("Delete success!", employeeService.deleteEmployee(ids));
        verify(employeeRepository, times(2)).findById(anyString());
        verify(validator).validateIdFormatEntity(ids.get(0), "EM");
        verify(validator).assumeRoleEmployee(any(Employee.class), anyBoolean());
        verify(employeeRepository, times(2)).countAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString());
        verify(employeeRepository).save(any(Employee.class));
        verify(assignmentService).getAssignmentCountByEmployeeId(ids.get(0));
        verify(employeeRepository).deleteById(anyString());
        verifyNoMoreInteractions(employeeRepository);
        verifyZeroInteractions(validator);
        verifyNoMoreInteractions(assignmentService);
    }

    @Test
    public void deleteEmployeeByListOfIdSuperiorIdHaveTwoSubordinateSuccess() {
        String superiorId = "EM036";
        List<String> ids = new ArrayList<>();
        ids.add("EM040");
        mockValidateId(true, ids.get(0));
        mockFindEmployeeByIdWithSuperiorId(true, "EM040", superiorId);
        mockEmployeeFoundPendingAssignment(false, ids.get(0));
        mockSuperiorFindSubordinateCount(true, superiorId);
        Employee superior = setSuperiorWithIdAndSuperiorIdNull(superiorId);
        mockFindEmployeeById(true, superiorId, superior);
        assertEquals("Delete success!", employeeService.deleteEmployee(ids));
        verify(employeeRepository, times(2)).findById(anyString());
        verify(validator).validateIdFormatEntity(anyString(), anyString());
        verify(employeeRepository, times(2)).countAllBySuperiorIdAndNameContainingIgnoreCase(anyString(), anyString());
        verify(assignmentService).getAssignmentCountByEmployeeId(ids.get(0));
        verify(employeeRepository).deleteById(anyString());
        verifyNoMoreInteractions(employeeRepository);
        verifyZeroInteractions(validator);
        verifyNoMoreInteractions(assignmentService);
    }

    @Test
    public void deleteEmployeeByListOfIdSuperiorIdNotFoundFailed() {
        String superiorId = "EM100";
        List<String> ids = new ArrayList<>();
        ids.add("EM040");
        mockValidateId(true, ids.get(0));
        mockEmployeeFoundPendingAssignment(false, ids.get(0));
        mockFindEmployeeByIdWithSuperiorId(true, ids.get(0), superiorId);
        Employee superior = setSuperiorWithIdAndSuperiorIdNull(superiorId);
        mockFindEmployeeById(false, superiorId, superior);
        try {
            employeeService.deleteEmployee(ids);
        } catch (RuntimeException e) {
            verify(employeeRepository, times(2)).findById(anyString());
            verify(validator).validateIdFormatEntity(ids.get(0), "EM");
            verify(assignmentService).getAssignmentCountByEmployeeId(ids.get(0));
            verifyNoMoreInteractions(employeeRepository);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(assignmentService);
        }
    }

    private void mockListOfEmployeeSubordinate(boolean found, String employeeId) {
        Employee employee = setAnotherEmployeeWithIdAndSuperiorId(employeeId);
        List<Employee> listOfSubordinate = new ArrayList<>();
        listOfSubordinate.add(employee);
        when(employeeRepository.findAllBySuperiorId(anyString()))
                .thenReturn(found ? listOfSubordinate : new ArrayList<>());
    }

    private void mockEmployeeFoundPendingAssignment(boolean found, String employeeId) {
        Map<String, Double> mapOfFilledAssignment = new HashMap<>();
        mapOfFilledAssignment.put("pendingAssignmentCount", 1.0);
        Map<String, Double> mapOfEmptyAssignment = new HashMap<>();
        mapOfEmptyAssignment.put("pendingAssignmentCount", 0.0);
        when(assignmentService.getAssignmentCountByEmployeeId(employeeId))
                .thenReturn(found ? mapOfFilledAssignment : mapOfEmptyAssignment);
    }

    private void mockEmployeeFindSubordinateCount(boolean found, String employeeId) {
        when(employeeRepository.countAllBySuperiorIdAndNameContainingIgnoreCase(employeeId, ""))
                .thenReturn(found ? 1.0f : 0f);
    }

    private void mockSuperiorFindSubordinateCount(boolean found, String employeeId) {
        when(employeeRepository.countAllBySuperiorIdAndNameContainingIgnoreCase(employeeId, ""))
                .thenReturn(found ? 2.0f : 0.0f);
    }

    private Paging setPagingWithDefaultValue() {
        Paging paging = new Paging();
        paging.setPageSize(5);
        paging.setPageNumber(1);
        paging.setSortedType("desc");
        paging.setSortedBy("updatedDate");
        return paging;
    }

    private void mockEmployeeEmailAlreadyTaken(String email) {
        Employee employee = new Employee();
        employee.setId("EM099");
        employee.setEmail(email);
        when(employeeRepository.findByEmail(email))
                .thenReturn(employee);
    }

    private void mockEmployeeListBasedOnNameAndPaging() {
        when(employeeRepository.findAllByNameContainingIgnoreCase(any(String.class), any(Pageable.class)))
                .thenReturn(employeePageList);
    }

    private void mockSuperiorListBasedOnSuperiorIdNameAndPaging() {
        when(employeeRepository.findAllBySuperiorIdAndNameContainingIgnoreCase(any(String.class),
                any(String.class), any(Pageable.class)))
                .thenReturn(employeePageList);
    }

    private void mockNullFieldEmployeeFound(boolean found) {
        when(validator.validateNullFieldEmployee(any(Employee.class)))
                .thenReturn(found ? "something" : null);
    }

    private Employee setEmployeeWithIdAndSuperiorIdNull() {
        Employee employee = new Employee();
        employee.setId("EM040");
        employee.setEmail("stelli@gdn-commerce.com");
        employee.setSuperiorId("-");
        employee.setDob("17/06/1998");
        employee.setName("Stelli");
        employee.setPosition("IT");
        employee.setDivision("Development");
        employee.setRole("SUPERIOR");
        return employee;
    }

    private Employee setAnotherEmployeeWithIdAndSuperiorId(String superiorId) {
        Employee employee = new Employee();
        employee.setId("EM039");
        employee.setEmail("karnando@gdn-commerce.com");
        employee.setSuperiorId(superiorId);
        employee.setDob("17/06/1998");
        employee.setName("Karnando");
        employee.setPosition("IT");
        employee.setDivision("Development");
        employee.setRole("EMPLOYEE");
        return employee;
    }

    private Employee setSuperiorWithIdAndSuperiorIdNull(String id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmail("david@gdn-commerce.com");
        employee.setSuperiorId("-");
        employee.setDob("17/06/1998");
        employee.setName("David WK");
        employee.setPosition("IT");
        employee.setDivision("Development");
        employee.setRole("EMPLOYEE");
        return employee;
    }

    private void setsuperiorIdWithinEmployee(Employee employee) {
        employee.setSuperiorId("EM036");
    }

    private Employee setEmployeeWithIdNullAndSuperiorIdNull() {
        Employee employee = new Employee();
        employee.setId(null);
        employee.setEmail("stelli@gdn-commerce.com");
        employee.setPassword("stelli");
        employee.setSuperiorId("null");
        employee.setDob("17/06/1998");
        employee.setName("Stelli");
        employee.setPosition("IT");
        employee.setDivision("Development");
        return employee;
    }

    private Employee setEmployeeWithIdNullAndSuperiorIdNullAndEmailNull() {
        Employee employee = new Employee();
        employee.setId(null);
        employee.setEmail(null);
        employee.setSuperiorId("null");
        employee.setDob("17/06/1998");
        employee.setName("Stelli");
        employee.setPosition("IT");
        employee.setDivision("Development");
        return employee;
    }

    private void setPaging(String sortedType) {
        this.paging.setPageNumber(1);
        this.paging.setPageSize(3);
        this.paging.setSortedBy("updatedDate");
        this.paging.setSortedType(sortedType);
    }

    private void mockFindEmployeeById(boolean found, String id, Employee employee) {
        if (found)
            when(employeeRepository.findById(id))
                    .thenReturn(Optional.ofNullable(employee));
        else
            when(employeeRepository.findById(id))
                    .thenThrow(new RuntimeException());
    }

    private void mockFindEmployeeByIdWithSuperiorId(boolean found, String id, String superiorId) {
        Employee employee = setEmployeeWithIdAndSuperiorIdNull();
        employee.setSuperiorId(superiorId);
        if (found)
            when(employeeRepository.findById(id))
                    .thenReturn(Optional.ofNullable(employee));
        else
            when(employeeRepository.findById(id))
                    .thenThrow(new RuntimeException());
    }

    private void mockFindEmployeeByEmail(boolean found, String email, Employee employee) {
        if (found)
            when(employeeRepository.findByEmail(email))
                    .thenReturn(employee);
        else
            when(employeeRepository.findByEmail(email))
                    .thenReturn(null);
    }

    private void mockValidateDOB(boolean valid, String dob) {
        when(validator.validateDobFormatEmployee(dob))
                .thenReturn(valid);
    }

    private void mockBcryptEncoderMatch(boolean valid) {
        when(encoder.matches(anyString(), anyString()))
                .thenReturn(valid);
    }

    private void mockValidateId(boolean valid, String id) {
        when(validator.validateIdFormatEntity(id, "EM"))
                .thenReturn(valid);
    }

    private void mockValidateEmail(boolean valid, String email) {
        when(validator.validateEmailFormatMember(email))
                .thenReturn(valid);
    }

    private void mockMapEmployee(boolean isNull, Employee e) {
        when(mapper.map(e, Employee.class))
                .thenReturn(isNull ? null : e);
    }

    private void mockSaveEmployee(Employee e) {
        when(employeeRepository.save(e)).thenReturn(e);
    }
}

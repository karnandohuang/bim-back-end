package com.inventory.services;

import com.inventory.models.Paging;
import com.inventory.models.entity.Assignment;
import com.inventory.models.entity.Employee;
import com.inventory.models.entity.Item;
import com.inventory.repositories.AssignmentRepository;
import com.inventory.services.assignment.AssignmentServiceImpl;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.helper.PagingHelper;
import com.inventory.services.item.ItemService;
import com.inventory.services.utils.exceptions.employee.EmployeeNotFoundException;
import com.inventory.services.utils.validators.AssignmentValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentServiceTest {

    @Mock
    AssignmentRepository assignmentRepository;

    @Mock
    ItemService itemService;

    @Mock
    EmployeeService employeeService;

    @Mock
    private AssignmentValidator validator;

    @Mock
    private PagingHelper pagingHelper;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private Paging paging = new Paging();
    private List<Assignment> assignmentList = mock(ArrayList.class);
    private Page<Assignment> assignmentPageList = mock(Page.class);

    @Test
    public void getAssignmentIdValidSuccess() {
        String id = "AT005";
        Assignment assignment = setAssignmentWithId();
        mockValidateId(true, id);
        mockFindAssignmentById(true, id);
        Assignment a = assignmentService.getAssignment(id);
        assertEquals(assignment, a);
        verify(validator).validateIdFormatEntity(anyString(), anyString());
        verify(assignmentRepository).findById(id);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void getAssignmentIdValidFailed() {
        String id = "AT099";
        mockValidateId(true, id);
        mockFindAssignmentById(false, id);
        try {
            assignmentService.getAssignment(id);
        } catch (RuntimeException e) {
            verify(assignmentRepository).findById(id);
            verifyNoMoreInteractions(assignmentRepository);
        }
    }

    @Test
    public void getAssignmentIdNotValidFailed() {
        String id = "99";
        mockValidateId(false, id);
        mockFindAssignmentById(false, id);
        try {
            assignmentService.getAssignment(id);
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(id, "AT");
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(assignmentRepository);
        }
    }

    @Test
    public void findAssignmentBasedOnFilterStatusAndPagingDescSuccess() {
        mockItemListBasedOnNameAndPaging();
        setPaging("desc");
        List<Assignment> returnAssignments = assignmentService.getAssignmentList("", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByStatusContaining(anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByStatus("");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findAssignmentBasedOnFilterStatusAndPagingAscSuccess() {
        mockItemListBasedOnNameAndPaging();
        setPaging("asc");
        List<Assignment> returnAssignments = assignmentService.getAssignmentList("", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByStatusContaining(anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByStatus("");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findAssignmentBasedOnFilterStatusNullAndPagingAscSuccess() {
        mockItemListBasedOnNameAndPaging();
        setPaging("asc");
        List<Assignment> returnAssignments = assignmentService.getAssignmentList(null, paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByStatusContaining(anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByStatus("");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeIdAndFilterStatusAndPagingDescSuccess() {
        mockItemListBasedOnNameAndPaging();
        setPaging("desc");
        String employeeId = "EM040";
        mockFindEmployeeByIdService(true, employeeId);
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
        List<Assignment> returnAssignments = assignmentService
                .getEmployeeAssignmentList(employeeId, "", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByEmployeeIdAndStatusContaining(anyString(),
                anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByEmployeeIdAndStatusContaining(employeeId, "");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeIdAndFilterStatusAndPagingAscSuccess() {
        mockItemListBasedOnNameAndPaging();
        setPaging("asc");
        String employeeId = "EM040";
        mockFindEmployeeByIdService(true, employeeId);
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
        List<Assignment> returnAssignments = assignmentService
                .getEmployeeAssignmentList(employeeId, "", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByEmployeeIdAndStatusContaining(anyString(),
                anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByEmployeeIdAndStatusContaining(employeeId, "");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    private void mockFindEmployeeByIdService(boolean found, String employeeId) {
        if (found)
            when(employeeService.getEmployee(employeeId))
                    .thenReturn(setEmployee());
        else
            when(employeeService.getEmployee(employeeId))
                    .thenThrow(new EmployeeNotFoundException(employeeId, "Id"));
    }

    private Employee setEmployee() {
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

    private void mockValidateId(boolean valid, String id) {
        when(validator.validateIdFormatEntity(id, "AT"))
                .thenReturn(valid ? true : false);
    }

    private void mockItemListBasedOnNameAndPaging() {
        when(assignmentRepository.findAllByStatusContaining(anyString(), any(Pageable.class)))
                .thenReturn(assignmentPageList);
    }

    private void mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging() {
        when(assignmentRepository.findAllByEmployeeIdAndStatusContaining(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(assignmentPageList);
    }

    private void setPaging(String sortedType) {
        this.paging.setPageNumber(1);
        this.paging.setPageSize(3);
        this.paging.setSortedBy("updatedDate");
        this.paging.setSortedType(sortedType);
    }

    private void mockFindAssignmentById(boolean found, String id) {
        Assignment assignment = setAssignmentWithId();
        if (found)
            when(assignmentRepository.findById(id))
                    .thenReturn(Optional.ofNullable(assignment));
        else
            when(assignmentRepository.findById(id))
                    .thenReturn(null);
    }

    private Assignment setAssignmentWithId() {
        Assignment assignment = new Assignment();
        assignment.setId("IM001");
        assignment.setEmployee(new Employee());
        assignment.setItem(new Item());
        assignment.setQty(9);
        assignment.setStatus("Pending");
        assignment.setNotes("");
        return assignment;
    }
}

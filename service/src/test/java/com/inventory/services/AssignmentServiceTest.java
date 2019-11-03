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
import com.inventory.services.member.MemberService;
import com.inventory.services.utils.exceptions.auth.MemberNotFoundException;
import com.inventory.services.utils.exceptions.employee.EmployeeNotFoundException;
import com.inventory.services.utils.exceptions.item.ItemNotFoundException;
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

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private MemberService memberService;

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
        String id = "AT001";
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
        mockAssignmentListBasedOnFilterStatusAndPaging();
        setPaging("desc");
        List<Assignment> returnAssignments = assignmentService.getAssignmentList("", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByStatusContaining(anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByStatusContaining("");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findAssignmentBasedOnFilterStatusAndPagingAscSuccess() {
        mockAssignmentListBasedOnFilterStatusAndPaging();
        setPaging("asc");
        List<Assignment> returnAssignments = assignmentService.getAssignmentList("", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByStatusContaining(anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByStatusContaining("");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findAssignmentBasedOnFilterStatusNullAndPagingAscSuccess() {
        mockAssignmentListBasedOnFilterStatusAndPaging();
        setPaging("asc");
        List<Assignment> returnAssignments = assignmentService.getAssignmentList(null, paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByStatusContaining(anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByStatusContaining("");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeIdAndFilterStatusAndPagingDescSuccess() {
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
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
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
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

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeIdAndFilterStatusNullAndPagingAscSuccess() {
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
        setPaging("asc");
        String employeeId = "EM040";
        mockFindEmployeeByIdService(true, employeeId);
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
        List<Assignment> returnAssignments = assignmentService
                .getEmployeeAssignmentList(employeeId, null, paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByEmployeeIdAndStatusContaining(anyString(),
                anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByEmployeeIdAndStatusContaining(employeeId, "");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeIdValidNotFoundFailed() {
        setPaging("asc");
        String employeeId = "EM040";
        mockFindEmployeeByIdService(false, employeeId);
        try {
            List<Assignment> returnAssignments = assignmentService
                    .getEmployeeAssignmentList(employeeId, null, paging);
        } catch (RuntimeException e) {
            verify(employeeService).getEmployee(employeeId);
            verifyNoMoreInteractions(employeeService);
            verifyZeroInteractions(assignmentRepository);
        }
    }

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeSuperiorIdAndFilterStatusAndPagingDescSuccess() {
        mockAssignmentListBasedOnEmployeeSuperiorIdAndFilterStatusAndPaging();
        setPaging("desc");
        String superiorId = "EM040";
        mockFindEmployeeByIdService(true, superiorId);
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
        List<Assignment> returnAssignments = assignmentService
                .getEmployeeSuperiorAssignmentList(superiorId, "", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByEmployeeSuperiorIdAndStatusContaining(anyString(),
                anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByEmployeeSuperiorIdAndStatusContaining(superiorId, "");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeSuperiorIdAndFilterStatusAndPagingAscSuccess() {
        mockAssignmentListBasedOnEmployeeSuperiorIdAndFilterStatusAndPaging();
        setPaging("asc");
        String superiorId = "EM040";
        mockFindEmployeeByIdService(true, superiorId);
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
        List<Assignment> returnAssignments = assignmentService
                .getEmployeeSuperiorAssignmentList(superiorId, "", paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByEmployeeSuperiorIdAndStatusContaining(anyString(),
                anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByEmployeeSuperiorIdAndStatusContaining(superiorId, "");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.ASC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeSuperiorIdAndFilterStatusNullAndPagingDescSuccess() {
        mockAssignmentListBasedOnEmployeeSuperiorIdAndFilterStatusAndPaging();
        setPaging("desc");
        String superiorId = "EM040";
        mockFindEmployeeByIdService(true, superiorId);
        mockAssignmentListBasedOnEmployeeIdAndFilterStatusAndPaging();
        List<Assignment> returnAssignments = assignmentService
                .getEmployeeSuperiorAssignmentList(superiorId, null, paging);
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);
        verify(assignmentRepository).findAllByEmployeeSuperiorIdAndStatusContaining(anyString(),
                anyString(), pageArgument.capture());
        verify(assignmentRepository).countAllByEmployeeSuperiorIdAndStatusContaining(superiorId, "");


        Sort actualSort = pageArgument.getValue().getSort();
        assertEquals(Sort.Direction.DESC, actualSort.getOrderFor(paging.getSortedBy()).getDirection());

        assertEquals(assignmentList.size(), returnAssignments.size());
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void findEmployeeAssignmentBasedOnEmployeeSuperiorIdValidNotFoundFailed() {
        setPaging("asc");
        String superiorId = "EM040";
        mockFindEmployeeByIdService(false, superiorId);
        try {
            List<Assignment> returnAssignments = assignmentService
                    .getEmployeeSuperiorAssignmentList(superiorId, null, paging);
        } catch (RuntimeException e) {
            verify(employeeService).getEmployee(superiorId);
            verifyNoMoreInteractions(employeeService);
            verifyZeroInteractions(assignmentRepository);
        }
    }

    @Test
    public void getAssignmentCountByEmployeeIdValidFoundSuccess() {
        String employeeId = "EM040";
        mockFindEmployeeByIdService(true, employeeId);
        mockGetAssignmentCountByEmployeeIdAllOne(true, employeeId);
        Map<String, Double> expected = getMapAssigmentCountByEmployeeIdReceivedCountOne();
        assertEquals(expected, assignmentService.getAssignmentCountByEmployeeId(employeeId));
        verify(employeeService).getEmployee(employeeId);
        verify(assignmentRepository, times(3))
                .countAllByEmployeeIdAndStatus(anyString(), anyString());
        verifyNoMoreInteractions(assignmentRepository);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    public void getAssignmentCountByEmployeeIdValidNotFoundFailed() {
        String employeeId = "EM040";
        mockFindEmployeeByIdService(false, employeeId);
        try {
            assignmentService.getAssignmentCountByEmployeeId(employeeId);
        } catch (RuntimeException e) {
            verify(employeeService).getEmployee(employeeId);
            verifyZeroInteractions(assignmentRepository);
            verifyNoMoreInteractions(employeeService);
        }
    }

    @Test
    public void getAssignmentCountByEmployeeIdAsAdminSuccess() {
        String employeeId = "ADMIN";
        mockFindEmployeeByIdService(true, employeeId);
        mockGetAssignmentCountByEmployeeIdAsAdminAllOne(true);
        Map<String, Double> expected = getMapAssigmentCountByEmployeeIdReceivedCountOne();
        assertEquals(expected, assignmentService.getAssignmentCountByEmployeeId(employeeId));
        verify(assignmentRepository, times(3))
                .countAllByStatusContaining(anyString());
        verifyNoMoreInteractions(assignmentRepository);
        verifyZeroInteractions(employeeService);
    }

    @Test
    public void getAssignmentCountByItemIdValidFoundAndStatusValidSuccess() {
        String itemId = "IM001";
        mockFindItemByIdService(true, itemId);
        mockGetAssignmentCountByItemId(true, itemId);
        mockValidateStatus(true, "Pending");
        Double expected = 1.0;
        assertEquals(expected, assignmentService.getAssignmentCountByItemIdAndStatus(itemId, "Pending"));
        verify(itemService).getItem(itemId);
        verify(validator).validateStatus("Pending");
        verify(assignmentRepository).countAllByItemIdAndStatus(anyString(), anyString());
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(assignmentRepository);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void getAssignmentCountByItemIdValidNotFoundFailed() {
        String itemId = "IM001";
        mockFindItemByIdService(false, itemId);
        try {
            assignmentService.getAssignmentCountByItemIdAndStatus(itemId, "Pending");
        } catch (RuntimeException e) {
            verify(itemService).getItem(itemId);
            verifyZeroInteractions(validator);
            verifyZeroInteractions(assignmentRepository);
            verifyNoMoreInteractions(itemService);
        }
    }

    @Test
    public void getAssignmentCountByItemIdValidFoundAndStatusNotValidFailed() {
        String itemId = "IM001";
        mockFindItemByIdService(true, itemId);
        mockGetAssignmentCountByItemId(true, itemId);
        mockValidateStatus(false, "Pending");
        try {
            assignmentService.getAssignmentCountByItemIdAndStatus(itemId, "Pending");
        } catch (RuntimeException e) {
            verify(itemService).getItem(itemId);
            verify(validator).validateStatus("Pending");
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(assignmentRepository);
            verifyNoMoreInteractions(itemService);
        }
    }

    @Test
    public void saveAssignmentItemIdValidFoundSuccess() {
        Assignment assignment = setAssignmentWithIdAndItem();
        mockFindItemByIdService(true, assignment.getItem().getId());
        assignmentService.saveAssignment(assignment);

        verify(itemService).getItem(assignment.getItem().getId());
        verify(itemService).changeItemQty(assignment);
        verify(validator).validateNullFieldAssignment(assignment);
        verify(assignmentRepository).save(assignment);
        verifyNoMoreInteractions(itemService);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void saveAssignmentNullFieldFoundFailed() {
        Assignment assignment = setAssignmentWithIdAndItem();
        mockNullFieldAssignment(true);
        try {
            assignmentService.saveAssignment(assignment);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldAssignment(any(Assignment.class));
            verifyZeroInteractions(assignmentRepository);
            verifyZeroInteractions(itemService);
            verifyNoMoreInteractions(validator);
        }
    }

    @Test
    public void changeAssignmentStatusAllValidSuccess() {
        List<String> ids = new ArrayList<>();
        ids.add("AT001");
        String status = "Rejected";
        String notes = "value currently unavailable!";
        String memberEmail = "admin2@gdn-commerce.com";
        mockValidateId(true, ids.get(0));
        mockMemberGetAdminMemberService(true, false, memberEmail);
        mockValidateStatus(true, status);
        mockValidateChangeStatus(true);
        mockFindAssignmentByIdWithItemAndEmployee(true, ids.get(0));
        assignmentService.changeStatusAssignments(ids, status, notes, memberEmail);
        verify(assignmentRepository).findById(ids.get(0));
        verify(validator).validateIdFormatEntity(ids.get(0), "AT");
        verify(memberService).getMemberRole(memberEmail);
        verify(validator).validateStatus(status);
        verify(validator).validateChangeStatus(anyString(), anyString());
        verify(assignmentRepository).save(any(Assignment.class));
        verifyNoMoreInteractions(memberService);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void changeAssignmentStatusSameChangeFailed() {
        List<String> ids = new ArrayList<>();
        ids.add("AT001");
        String status = "Pending";
        String notes = "value currently unavailable!";
        String memberEmail = "admin2@gdn-commerce.com";
        mockValidateId(true, ids.get(0));
        mockMemberGetAdminMemberService(true, false, memberEmail);
        mockValidateStatus(true, status);
        mockValidateChangeStatus(false);
        mockFindAssignmentByIdWithItemAndEmployee(true, ids.get(0));
        try {
            assignmentService.changeStatusAssignments(ids, status, notes, memberEmail);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            verify(assignmentRepository).findById(ids.get(0));
            verify(validator).validateIdFormatEntity(ids.get(0), "AT");
            verify(memberService).getMemberRole(memberEmail);
            verify(validator).validateStatus(status);
            verifyNoMoreInteractions(memberService);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(assignmentRepository);
        }
    }

    @Test
    public void changeAssignmentStatusOrderChangeWrongFailed() {
        List<String> ids = new ArrayList<>();
        ids.add("AT001");
        String status = "Received";
        String notes = "value currently unavailable!";
        String memberEmail = "admin2@gdn-commerce.com";
        mockValidateId(true, ids.get(0));
        mockMemberGetAdminMemberService(true, false, memberEmail);
        mockValidateStatus(true, status);
        mockValidateChangeStatus(false);
        mockFindAssignmentByIdWithItemAndEmployee(true, ids.get(0));
        try {
            assignmentService.changeStatusAssignments(ids, status, notes, memberEmail);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            verify(assignmentRepository).findById(ids.get(0));
            verify(validator).validateIdFormatEntity(ids.get(0), "AT");
            verify(validator).validateChangeStatus(anyString(), anyString());
            verify(memberService).getMemberRole(memberEmail);
            verify(validator).validateStatus(status);
            verifyNoMoreInteractions(memberService);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(assignmentRepository);
        }
    }

    @Test
    public void changeAssignmentStatusChangeNotValidFailed() {
        List<String> ids = new ArrayList<>();
        ids.add("AT001");
        String status = "abc";
        String notes = "value currently unavailable!";
        String memberEmail = "admin2@gdn-commerce.com";
        mockValidateId(true, ids.get(0));
        mockMemberGetAdminMemberService(true, false, memberEmail);
        mockValidateStatus(false, status);
        mockFindAssignmentByIdWithItemAndEmployee(true, ids.get(0));
        try {
            assignmentService.changeStatusAssignments(ids, status, notes, memberEmail);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            verify(assignmentRepository).findById(ids.get(0));
            verify(validator).validateIdFormatEntity(ids.get(0), "AT");
            verify(memberService).getMemberRole(memberEmail);
            verify(validator).validateStatus(status);
            verifyNoMoreInteractions(memberService);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(assignmentRepository);
        }
    }

    @Test
    public void changeAssignmentStatusAllValidSuperiorSuccess() {
        List<String> ids = new ArrayList<>();
        ids.add("AT001");
        String status = "abc";
        String notes = "value currently unavailable!";
        String memberEmail = "david@gdn-commerce.com";
        mockGetSuperiorByEmail(true, memberEmail);
        mockValidateId(true, ids.get(0));
        mockMemberGetAdminMemberService(false, true, memberEmail);
        mockValidateStatus(true, status);
        mockFindAssignmentByIdWithItemAndSuperior(true, ids.get(0));
        mockValidateChangeStatus(true);
        assignmentService.changeStatusAssignments(ids, status, notes, memberEmail);
        verify(assignmentRepository).findById(ids.get(0));
        verify(validator).validateIdFormatEntity(ids.get(0), "AT");
        verify(validator).validateChangeStatus(anyString(), anyString());
        verify(memberService).getMemberRole(memberEmail);
        verify(validator).validateStatus(status);
        verify(assignmentRepository).save(any(Assignment.class));
        verifyNoMoreInteractions(memberService);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(assignmentRepository);
    }

    @Test
    public void changeAssignmentStatusSuperiorIdAndEmployeeSameFailed() {
        List<String> ids = new ArrayList<>();
        ids.add("AT001");
        String status = "abc";
        String notes = "value currently unavailable!";
        String memberEmail = "stelli@gdn-commerce.com";
        mockGetEmployeeByEmail(true, memberEmail);
        mockValidateId(true, ids.get(0));
        mockMemberGetAdminMemberService(false, true, memberEmail);
        mockValidateStatus(true, status);
        mockFindAssignmentByIdWithItemAndSuperior(true, ids.get(0));
        mockValidateChangeStatus(true);
        try {
            assignmentService.changeStatusAssignments(ids, status, notes, memberEmail);
        } catch (RuntimeException e) {
            verify(assignmentRepository).findById(ids.get(0));
            verify(validator).validateIdFormatEntity(ids.get(0), "AT");
            verify(validator).validateChangeStatus(anyString(), anyString());
            verify(memberService).getMemberRole(memberEmail);
            verify(validator).validateStatus(status);
            verifyNoMoreInteractions(memberService);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(assignmentRepository);
        }
    }

    @Test
    public void changeAssignmentStatusSuperiorIdNotOfEmployeeFailed() {
        List<String> ids = new ArrayList<>();
        ids.add("AT001");
        String status = "abc";
        String notes = "value currently unavailable!";
        String memberEmail = "david@gdn-commerce.com";
        mockGetAnotherSuperiorByEmail(true, memberEmail);
        mockValidateId(true, ids.get(0));
        mockMemberGetAdminMemberService(false, true, memberEmail);
        mockValidateStatus(true, status);
        mockFindAssignmentByIdWithItemAndSuperior(true, ids.get(0));
        mockValidateChangeStatus(true);
        try {
            assignmentService.changeStatusAssignments(ids, status, notes, memberEmail);
        } catch (RuntimeException e) {
            verify(assignmentRepository).findById(ids.get(0));
            verify(validator).validateIdFormatEntity(ids.get(0), "AT");
            verify(validator).validateChangeStatus(anyString(), anyString());
            verify(memberService).getMemberRole(memberEmail);
            verify(validator).validateStatus(status);
            verifyNoMoreInteractions(memberService);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(assignmentRepository);
        }
    }

    @Test
    public void listOfRecoveredItemsAssignmentAllValidTwoAssignmentOneItemSuccess() {
        Map<String, Integer> expectedListOfRecoveredItems = new HashMap<>();
        expectedListOfRecoveredItems.put("IM001", 18);
        List<String> ids = new ArrayList<>();
        ids.add("AT001");
        ids.add("AT002");
        for (String id : ids)
            mockValidateId(true, id);
        mockFindAssignmentByIdWithItemAndEmployee(true, ids.get(0));
        mockFindAnotherAssignmentByIdWithItemAndEmployee(true, ids.get(1));
        mockFindItemByIdService(true, "IM001");
        assertEquals(expectedListOfRecoveredItems, assignmentService.getRecoveredItems(ids));
        verify(validator, times(2)).validateIdFormatEntity(anyString(), anyString());
        verify(assignmentRepository).findById(ids.get(0));
    }

    private void mockValidateStatus(boolean valid, String status) {
        when(validator.validateStatus(status))
                .thenReturn(valid);
    }

    private void mockNullFieldAssignment(boolean found) {
        when(validator.validateNullFieldAssignment(any(Assignment.class)))
                .thenReturn(found ? "something" : null);
    }

    private void mockValidateChangeStatus(boolean valid) {
        when(validator.validateChangeStatus(anyString(), anyString()))
                .thenReturn(valid);
    }

    private Map<String, Double> getMapAssigmentCountByEmployeeIdReceivedCountOne() {
        Map<String, Double> map = new HashMap<>();
        map.put("pendingAssignmentCount", 1.0);
        map.put("pendingHandoverCount", 1.0);
        map.put("receivedCount", 1.0);
        return map;
    }

    private void mockGetAssignmentCountByEmployeeIdAllOne(boolean found, String employeeId) {
        when(assignmentRepository.countAllByEmployeeIdAndStatus(employeeId, "Pending"))
                .thenReturn(found ? 1.0 : 0.0);
        when(assignmentRepository.countAllByEmployeeIdAndStatus(employeeId, "Approved"))
                .thenReturn(found ? 1.0 : 0.0);
        when(assignmentRepository.countAllByEmployeeIdAndStatus(employeeId, "Received"))
                .thenReturn(found ? 1.0 : 0.0);
    }

    private void mockGetAssignmentCountByEmployeeIdAsAdminAllOne(boolean found) {
        when(assignmentRepository.countAllByStatusContaining("Pending"))
                .thenReturn(found ? 1.0f : 0.0f);
        when(assignmentRepository.countAllByStatusContaining("Approved"))
                .thenReturn(found ? 1.0f : 0.0f);
        when(assignmentRepository.countAllByStatusContaining("Received"))
                .thenReturn(found ? 1.0f : 0.0f);
    }

    private void mockMemberGetAdminMemberService(boolean admin, boolean employee, String email) {
        if (admin)
            when(memberService.getMemberRole(email))
                    .thenReturn("ADMIN");
        else if (employee)
            when(memberService.getMemberRole(email))
                    .thenReturn("SUPERIOR");
        else
            when(memberService.getMemberRole(email))
                    .thenThrow(new MemberNotFoundException(email));
    }

    private void mockGetSuperiorByEmail(boolean found, String email) {
        when(employeeService.getEmployeeByEmail(email))
                .thenReturn(found ? setSuperior() : null);
    }

    private void mockGetAnotherSuperiorByEmail(boolean found, String email) {
        Employee superior = setSuperior();
        superior.setId("EM033");
        when(employeeService.getEmployeeByEmail(email))
                .thenReturn(found ? superior : null);
    }

    private void mockGetEmployeeByEmail(boolean found, String email) {
        when(employeeService.getEmployeeByEmail(email))
                .thenReturn(found ? setEmployeeWithSuperiorId() : null);
    }

    private void mockFindEmployeeByIdService(boolean found, String employeeId) {
        if (found)
            when(employeeService.getEmployee(employeeId))
                    .thenReturn(setEmployee());
        else
            when(employeeService.getEmployee(employeeId))
                    .thenThrow(new EmployeeNotFoundException(employeeId, "Id"));
    }

    private void mockFindItemByIdService(boolean found, String itemId) {
        if (found)
            when(itemService.getItem(itemId))
                    .thenReturn(setItemWithId());
        else
            when(itemService.getItem(itemId))
                    .thenThrow(new ItemNotFoundException(itemId, "Id"));
    }

    private void mockGetAssignmentCountByItemId(boolean found, String itemId) {
        when(assignmentRepository.countAllByItemIdAndStatus(anyString(), anyString()))
                .thenReturn(found ? 1.0f : 0.0f);
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

    private Employee setEmployeeWithSuperiorId() {
        Employee employee = new Employee();
        employee.setId("EM040");
        employee.setEmail("stelli@gdn-commerce.com");
        employee.setSuperiorId("EM036");
        employee.setDob("17/06/1998");
        employee.setName("Stelli");
        employee.setPosition("IT");
        employee.setDivision("Development");
        employee.setRole("SUPERIOR");
        return employee;
    }

    private Employee setSuperior() {
        Employee employee = new Employee();
        employee.setId("EM036");
        employee.setEmail("stelli@gdn-commerce.com");
        employee.setSuperiorId("EM036");
        employee.setDob("17/06/1998");
        employee.setName("Stelli");
        employee.setPosition("IT");
        employee.setDivision("Development");
        employee.setRole("SUPERIOR");
        return employee;
    }

    private Item setItemWithId() {
        Item item = new Item();
        item.setId("IM001");
        item.setName("Macbook Pro 15");
        item.setPrice(10000000);
        item.setQty(9);
        item.setLocation("Thamrin Office");
        item.setImageUrl("null");
        return item;
    }

    private void mockValidateId(boolean valid, String id) {
        when(validator.validateIdFormatEntity(id, "AT"))
                .thenReturn(valid ? true : false);
    }

    private void mockAssignmentListBasedOnFilterStatusAndPaging() {
        when(assignmentRepository.findAllByStatusContaining(anyString(), any(Pageable.class)))
                .thenReturn(assignmentPageList);
    }

    private void mockAssignmentListBasedOnEmployeeSuperiorIdAndFilterStatusAndPaging() {
        when(assignmentRepository.findAllByEmployeeSuperiorIdAndStatusContaining(anyString(), anyString(), any(Pageable.class)))
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
                    .thenThrow(new NullPointerException());
    }

    private void mockFindAssignmentByIdWithItemAndEmployee(boolean found, String id) {
        Assignment assignment = setAssignmentWithIdAndItem();
        assignment.setEmployee(setEmployee());
        if (found)
            when(assignmentRepository.findById(id))
                    .thenReturn(Optional.ofNullable(assignment));
        else
            when(assignmentRepository.findById(id))
                    .thenThrow(new NullPointerException());
    }

    private void mockFindAnotherAssignmentByIdWithItemAndEmployee(boolean found, String id) {
        Assignment assignment = setAssignmentWithIdAndItem();
        assignment.setEmployee(setEmployee());
        assignment.setId("AT002");
        if (found)
            when(assignmentRepository.findById(id))
                    .thenReturn(Optional.ofNullable(assignment));
        else
            when(assignmentRepository.findById(id))
                    .thenThrow(new NullPointerException());
    }

    private void mockFindAssignmentByIdWithItemAndSuperior(boolean found, String id) {
        Assignment assignment = setAssignmentWithIdAndItem();
        assignment.setEmployee(setEmployeeWithSuperiorId());
        if (found)
            when(assignmentRepository.findById(id))
                    .thenReturn(Optional.ofNullable(assignment));
        else
            when(assignmentRepository.findById(id))
                    .thenThrow(new NullPointerException());
    }

    private Assignment setAssignmentWithId() {
        Assignment assignment = new Assignment();
        assignment.setId("AT001");
        assignment.setEmployee(new Employee());
        assignment.setItem(new Item());
        assignment.setQty(9);
        assignment.setStatus("Pending");
        assignment.setNotes("");
        return assignment;
    }

    private Assignment setAssignmentWithIdAndItem() {
        Assignment assignment = new Assignment();
        assignment.setId("IM001");
        assignment.setEmployee(new Employee());
        assignment.setItem(setItemWithId());
        assignment.setQty(9);
        assignment.setStatus("Pending");
        assignment.setNotes("");
        return assignment;
    }
}

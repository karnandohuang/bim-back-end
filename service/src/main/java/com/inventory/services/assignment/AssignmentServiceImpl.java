package com.inventory.services.assignment;

import com.inventory.models.Paging;
import com.inventory.models.entity.Assignment;
import com.inventory.models.entity.Item;
import com.inventory.repositories.AssignmentRepository;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.helper.PagingHelper;
import com.inventory.services.item.ItemService;
import com.inventory.services.member.MemberService;
import com.inventory.services.utils.exceptions.EntityNullFieldException;
import com.inventory.services.utils.exceptions.assignment.*;
import com.inventory.services.utils.validators.AssignmentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inventory.services.utils.constants.ExceptionConstant.ASSIGNMENT_STATUS_WRONG_FORMAT_ERROR;
import static com.inventory.services.utils.constants.ExceptionConstant.ID_WRONG_FORMAT_ERROR;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentServiceImpl.class);
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AssignmentValidator validator;

    @Autowired
    private PagingHelper pagingHelper;

    private static final String ASSIGNMENT_ID_PREFIX = "AT";
    @Autowired
    private MemberService memberService;

    @Override
    public Assignment getAssignment(String id) {
        logger.info("getting asssignment of id :" + id);
        if (!validator.validateIdFormatEntity(id, ASSIGNMENT_ID_PREFIX))
            throw new AssignmentFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);
        logger.info("passed validator check!");
        try {
            return assignmentRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new AssignmentNotFoundException(id, "Id");
        }
    }

    @Override
    @Transactional
    public List<Assignment> getAssignmentList(String filterStatus, Paging paging) {
        if (filterStatus == null)
            filterStatus = "";
        List<Assignment> listOfAssignment;
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
        listOfAssignment = assignmentRepository.findAllByStatusContaining(filterStatus, pageRequest).getContent();
        float totalRecords = assignmentRepository.countAllByStatus(filterStatus);
        pagingHelper.setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfAssignment;
    }

    @Override
    @Transactional
    public List<Assignment> getEmployeeAssignmentList(String employeeId, String filterStatus, Paging paging) {
        try {
            employeeService.getEmployee(employeeId);
        } catch (RuntimeException e) {
            throw e;
        }
        if (filterStatus == null)
            filterStatus = "";
        List<Assignment> listOfAssignment;
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
        listOfAssignment = assignmentRepository.findAllByEmployeeIdAndStatusContaining(employeeId, filterStatus, pageRequest).getContent();
        float totalRecords = assignmentRepository.countAllByEmployeeIdAndStatusContaining(employeeId, filterStatus);
        pagingHelper.setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfAssignment;
    }

    @Override
    @Transactional
    public List<Assignment> getEmployeeSuperiorAssignmentList(String superiorId, String filterStatus, Paging paging) {
        try {
            employeeService.getEmployee(superiorId);
        } catch (RuntimeException e) {
            throw e;
        }
        if (filterStatus == null)
            filterStatus = "";
        List<Assignment> listOfAssignment;
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
        listOfAssignment = assignmentRepository.findAllByEmployeeSuperiorIdAndStatusContaining(superiorId, filterStatus, pageRequest).getContent();
        float totalRecords = assignmentRepository.countAllByEmployeeSuperiorIdAndStatusContaining(superiorId, filterStatus);
        pagingHelper.setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfAssignment;
    }

    @Override
    @Transactional
    public Map<String, Double> getAssignmentCountByEmployeeId(String employeeId) {

        Double pendingHandoverCount;
        Double pendingAssignmentCount;
        Double receivedCount;
        if (employeeId.equals("ADMIN")) {
            pendingAssignmentCount = Math.ceil(assignmentRepository.
                    countAllByStatus("Pending"));
            pendingHandoverCount = Math.ceil(assignmentRepository.
                    countAllByStatus("Approved"));
            receivedCount = Math.ceil(assignmentRepository.
                    countAllByStatus("Received"));
        } else {
            try {
                employeeService.getEmployee(employeeId);
            } catch (RuntimeException e) {
                throw e;
            }

            pendingAssignmentCount = Math.ceil(assignmentRepository.
                    countAllByEmployeeIdAndStatus(employeeId, "Pending"));
            pendingHandoverCount = Math.ceil(assignmentRepository.
                    countAllByEmployeeIdAndStatus(employeeId, "Approved"));
            receivedCount = Math.ceil(assignmentRepository.
                    countAllByEmployeeIdAndStatus(employeeId, "Received"));
        }

        Map<String, Double> listOfCount = new HashMap<>();

        listOfCount.put("pendingAssignmentCount", pendingAssignmentCount);
        listOfCount.put("pendingHandoverCount", pendingHandoverCount);
        listOfCount.put("receivedCount", receivedCount);

        return listOfCount;
    }

    @Override
    @Transactional
    public Double getAssignmentCountByItemIdAndStatus(String itemId, String status) {
        try {
            itemService.getItem(itemId);
        } catch (RuntimeException e) {
            throw e;
        }
        if (!validator.validateStatus(status))
            throw new AssignmentFieldWrongFormatException(ASSIGNMENT_STATUS_WRONG_FORMAT_ERROR);
        return Math.ceil(assignmentRepository.countAllByItemIdAndStatus(itemId, status));
    }

    @Override
    @Transactional
    public Assignment saveAssignment(Assignment assignment) throws RuntimeException {
        String nullFieldAssignment = validator.validateNullFieldAssignment(assignment);
        if (nullFieldAssignment != null)
            throw new EntityNullFieldException(nullFieldAssignment);
        Item item = itemService.getItem(assignment.getItem().getId());
        int qty = assignment.getQty();
        itemService.changeItemQty(assignment);
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public String changeStatusAssignments(List<String> ids, String status, String notes, String memberEmail) {
        for (String id : ids) {
            Assignment assignment;
            logger.info("authorized by : " + memberEmail);
            assignment = this.getAssignment(id);
            String memberId;
            String role = memberService.getMemberRole(memberEmail);
            if (role.equals("ADMIN"))
                memberId = null;
            else
                memberId = employeeService.getEmployeeByEmail(memberEmail).getId();

            if (!validator.validateStatus(status))
                throw new AssignmentFieldWrongFormatException(ASSIGNMENT_STATUS_WRONG_FORMAT_ERROR);

            else if (assignment.getStatus().equals(status))
                throw new AssignmentStatusIsSameException(status);

            else if (!validator.validateChangeStatus(assignment.getStatus(), status))
                throw new AssignmentStatusOrderWrongException(assignment.getStatus(), status);

            else if (assignment.getEmployee().getId().equals(memberId) && memberId != null)
                throw new AssignmentAuthorizedSameEmployeeException(assignment.getId(), memberId);

            else if (!assignment.getEmployee().getSuperiorId().equals(memberId) && memberId != null)
                throw new AssignmentAuthorizedDifferentSuperiorException(memberId);

            assignment.setStatus(status);
            assignment.setNotes(notes);
            assignmentRepository.save(assignment);
        }
        return "Change status success";
    }

    @Override
    @Transactional
    public Map<String, Integer> getRecoveredItems(List<String> ids) {
        Map<String, Integer> listOfRecoveredItems = new HashMap<>();
        int qty = 0;
        for(String id : ids) {
            qty = 0;
            Assignment assignment;
            assignment = this.getAssignment(id);
            if (listOfRecoveredItems.get(assignment.getItem().getId()) != null)
                qty = listOfRecoveredItems.get(assignment.getItem().getId());
            listOfRecoveredItems.put(assignment.getItem().getId(), qty + assignment.getQty());
        }
        return listOfRecoveredItems;
    }
}

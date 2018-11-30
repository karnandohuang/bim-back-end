package com.inventory.services.assignment;

import com.inventory.models.Assignment;
import com.inventory.models.Paging;
import com.inventory.repositories.AssignmentRepository;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.exceptions.EntityNullFieldException;
import com.inventory.services.exceptions.assignment.AssignmentFieldWrongFormatException;
import com.inventory.services.exceptions.assignment.AssignmentNotFoundException;
import com.inventory.services.exceptions.assignment.AssignmentStatusIsSameException;
import com.inventory.services.item.ItemService;
import com.inventory.services.validators.AssignmentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    AssignmentRepository AssignmentRepository;

    @Autowired
    ItemService itemService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    private AssignmentValidator validator;


    @Override
    public Assignment getAssignment(String id) {
        return AssignmentRepository.findById(id).get();
    }

    @Override
    @Transactional
    public List<Assignment> getAssignmentList(Paging paging) {
        List<Assignment> listOfAssignment;
        if (paging.getSortedType().matches("desc")) {
            listOfAssignment = AssignmentRepository.findAll(PageRequest.of(paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.DESC,
                    paging.getSortedBy())).getContent();
        } else {
            listOfAssignment = AssignmentRepository.findAll(PageRequest.of(paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.ASC,
                    paging.getSortedBy())).getContent();
        }
        float totalRecords = (float) AssignmentRepository.count();
        setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfAssignment;
    }

    @Override
    @Transactional
    public List<Assignment> getEmployeeAssignmentList(String employeeId, Paging paging) throws RuntimeException {
        try {
            employeeService.getEmployee(employeeId);
        } catch (RuntimeException e) {
            throw e;
        }
        List<Assignment> listOfAssignment;
        if (paging.getSortedType().matches("desc")) {
            listOfAssignment = AssignmentRepository.findAllByEmployeeId(employeeId,
                    PageRequest.of(paging.getPageNumber() - 1,
                            paging.getPageSize(),
                            Sort.Direction.DESC,
                            paging.getSortedBy())).getContent();
        } else {
            listOfAssignment = AssignmentRepository.findAllByEmployeeId(employeeId,
                    PageRequest.of(paging.getPageNumber() - 1,
                            paging.getPageSize(),
                            Sort.Direction.ASC,
                            paging.getSortedBy())).getContent();
        }
        float totalRecords = AssignmentRepository.countAllByEmployeeId(employeeId);
        setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfAssignment;
    }

    private void setPagingTotalRecordsAndTotalPage(Paging paging, float totalRecords) {
        paging.setTotalRecords((int) totalRecords);
        double totalPage = (int) Math.ceil((totalRecords / paging.getPageSize()));
        paging.setTotalPage((int) totalPage);
    }

    @Override
    @Transactional
    public Double getAssignmentCountByEmployeeIdAndStatus(String employeeId, String status) throws RuntimeException {
        try {
            employeeService.getEmployee(employeeId);
        } catch (RuntimeException e) {
            throw e;
        }
        if (!validator.validateStatus(status))
            throw new AssignmentFieldWrongFormatException("Status is not in the right format");
        Double count = Math.ceil(AssignmentRepository.countAllByEmployeeIdAndStatus(employeeId, status));
        return count;
    }

    @Override
    @Transactional
    public Double getAssignmentCountByItemIdAndStatus(String itemId, String status) throws RuntimeException {
        try {
            itemService.getItem(itemId);
        } catch (RuntimeException e) {
            throw e;
        }
        if (!validator.validateStatus(status))
            throw new AssignmentFieldWrongFormatException("Status is not in the right format");
        Double count = Math.ceil(AssignmentRepository.countAllByItemIdAndStatus(itemId, status));
        return count;
    }

    @Override
    @Transactional
    public Assignment saveAssignment(Assignment assignment) throws RuntimeException {
        String nullFieldAssignment = validator.validateNullFieldAssignment(assignment);
        if (nullFieldAssignment != null)
            throw new EntityNullFieldException(nullFieldAssignment);
        return AssignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public String changeStatusAssignments(List<String> ids, String status, String notes) throws RuntimeException {
        List<String> listOfErrors = new ArrayList<>();
        for (String id : ids) {
            Assignment assignment;
            try {
                assignment = AssignmentRepository.findById(id).get();
            } catch (RuntimeException e) {
                throw new AssignmentNotFoundException("id : " + id + " is not exist");
            }
            if (!validator.validateStatus(status))
                throw new AssignmentFieldWrongFormatException("Status is not in the right format");
            else if (assignment.getStatus().equals(status))
                throw new AssignmentStatusIsSameException("Status is already " + status);
                else {
                assignment.setStatus(status);
                assignment.setNotes(notes);
                AssignmentRepository.save(assignment);
            }
        }
        return "Change status success";
    }

    @Override
    @Transactional
    public Map<String, Integer> getRecoveredItems(List<String> ids) throws RuntimeException {
        Map<String, Integer> listOfRecoveredItems = new HashMap<>();
        int qty = 0;
        for(String id : ids) {
            qty = 0;
            Assignment assignment;
            try {
                assignment = AssignmentRepository.findById(id).get();
            } catch (RuntimeException e) {
                throw new AssignmentNotFoundException("id : " + id + " is not exist");
            }
            if (listOfRecoveredItems.get(assignment.getItem().getId()) != null)
                qty = listOfRecoveredItems.get(assignment.getItem().getId());
            listOfRecoveredItems.put(assignment.getItem().getId(), qty + assignment.getQty());
        }
        return listOfRecoveredItems;
    }
}

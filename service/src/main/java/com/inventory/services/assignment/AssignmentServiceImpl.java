package com.inventory.services.assignment;

import com.inventory.models.Assignment;
import com.inventory.models.Paging;
import com.inventory.repositories.AssignmentRepository;
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
    public List<Assignment> getEmployeeAssignmentList(String employeeId, Paging paging) {
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
    public Double getAssignmentCountByEmployeeIdAndStatus(String employeeId, String status) {
        Double count = Math.ceil(AssignmentRepository.countAllByEmployeeIdAndStatus(employeeId, status));
        return count;
    }

    @Override
    @Transactional
    public Assignment saveAssignment(Assignment Assignment) {
        return AssignmentRepository.save(Assignment);
    }

    @Override
    @Transactional
    public List<String> deleteAssignments(List<String> ids) {
        List<String> listOfNotFoundIds = new ArrayList<>();
        for (String id : ids) {
            try {
                AssignmentRepository.deleteById(id);
            } catch (NullPointerException e) {
                listOfNotFoundIds.add("id " + id + " not found");
            }
        }
        return listOfNotFoundIds;
    }

    @Override
    @Transactional
    public List<String> changeStatusAssignments(List<String> ids, String status, String notes) {
        List<String> listOfErrors = new ArrayList<>();
        for (String id : ids) {
            try {
                Assignment Assignment = AssignmentRepository.findById(id).get();
                if (Assignment.getStatus().equals(status))
                    listOfErrors.add("failed because status is already" + status);
                else {
                    Assignment.setStatus(status);
                    Assignment.setNotes(notes);
                    AssignmentRepository.save(Assignment);
                }
            } catch (NullPointerException e) {
                listOfErrors.add("id " + id + " not found");
            }
        }
        return listOfErrors;
    }

    @Override
    @Transactional
    public Map<String, Integer> getRecoveredItems(List<String> ids){
        Map<String, Integer> listOfRecoveredItems = new HashMap<>();
        int qty = 0;
        for(String id : ids) {
            qty = 0;
            Assignment Assignment = AssignmentRepository.findById(id).get();
            if (listOfRecoveredItems.get(Assignment.getItemId()) != null)
                qty = listOfRecoveredItems.get(Assignment.getItemId());
            listOfRecoveredItems.put(Assignment.getItemId(), qty + Assignment.getQty());
        }
        return listOfRecoveredItems;
    }
}

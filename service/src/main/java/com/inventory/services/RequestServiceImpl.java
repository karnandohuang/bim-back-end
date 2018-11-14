package com.inventory.services;

import com.inventory.models.Paging;
import com.inventory.models.Request;
import com.inventory.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    RequestRepository requestRepository;


    @Override
    public Request getRequest(String id) {
        return requestRepository.findById(id).get();
    }

    private List<Request> getRequestListFromRepository(Paging paging) {
        List<Request> listOfRequest;
        if (paging.getSortedType().matches("desc")) {
            listOfRequest = requestRepository.findAll(new Sort(Sort.Direction.DESC, paging.getSortedBy()));
        } else {
            listOfRequest = requestRepository.findAll(new Sort(Sort.Direction.ASC, paging.getSortedBy()));
        }
        return listOfRequest;
    }

    private List<Request> getEmployeeRequestListFromRepository(String employeeId, Paging paging) {
        List<Request> listOfRequest;
        if (paging.getSortedType().matches("desc")) {
            listOfRequest = requestRepository.findAllByEmployeeId(employeeId,
                    new Sort(Sort.Direction.DESC, paging.getSortedBy()));
        } else {
            listOfRequest = requestRepository.findAllByEmployeeId(employeeId,
                    new Sort(Sort.Direction.ASC, paging.getSortedBy()));
        }
        return listOfRequest;
    }


    @Override
    @Transactional
    public List<Request> getRequestList(Paging paging) {
        List<Request> listOfSortedRequest = new ArrayList<>();
        List<Request> listOfRequest = getRequestListFromRepository(paging);
        int totalRecords = listOfRequest.size();
        paging.setTotalRecords(totalRecords);
        int offset = (paging.getPageSize() * (paging.getPageNumber()-1));
        int totalPage = (totalRecords / paging.getPageSize());
        paging.setTotalPage(totalPage);
        for(int i = 0; i < paging.getPageSize(); i++){
            if ((offset + i) >= totalRecords) {
                break;
            }
            listOfSortedRequest.add(listOfRequest.get((offset + i)));
        }
        return listOfSortedRequest;
    }

    @Override
    public List<Request> getEmployeeRequestList(String employeeId, Paging paging) {
        List<Request> listOfSortedRequest = new ArrayList<>();
        List<Request> listOfRequest = getEmployeeRequestListFromRepository(employeeId, paging);
        int totalRecords = listOfRequest.size();
        paging.setTotalRecords(totalRecords);
        int offset = (paging.getPageSize() * (paging.getPageNumber() - 1));
        int totalPage = (int) Math.ceil((float) (totalRecords / paging.getPageSize()));
        paging.setTotalPage(totalPage);
        for (int i = 0; i < paging.getPageSize(); i++) {
            if ((offset + i) >= totalRecords) {
                break;
            }
            listOfSortedRequest.add(listOfRequest.get((offset + i)));
        }
        return listOfSortedRequest;
    }

    @Override
    @Transactional
    public Request saveRequest(Request request) {
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public List<String> deleteRequests(List<String> ids) {
        List<String> listOfNotFoundIds = new ArrayList<>();
        for (String id : ids) {
            try {
                requestRepository.deleteById(id);
            } catch (NullPointerException e) {
                listOfNotFoundIds.add("id " + id + " not found");
            }
        }
        return listOfNotFoundIds;
    }

    @Override
    public List<String> changeStatusRequests(List<String> ids, String status, String notes) {
        List<String> listOfErrors = new ArrayList<>();
        for (String id : ids) {
            try {
                Request request = requestRepository.findById(id).get();
                if (request.getStatus().equals(status))
                    listOfErrors.add("failed because status is already" + status);
                else {
                    request.setStatus(status);
                    request.setNotes(notes);
                    requestRepository.save(request);
                }
            } catch (NullPointerException e) {
                listOfErrors.add("id " + id + " not found");
            }
        }
        return listOfErrors;
    }

    @Override
    public Map<String, Integer> getRecoveredItems(List<String> ids){
        Map<String, Integer> listOfRecoveredItems = new HashMap<>();
        int qty = 0;
        for(String id : ids) {
            qty = 0;
            Request request = requestRepository.findById(id).get();
            if (listOfRecoveredItems.get(request.getItemId()) != null)
                qty = listOfRecoveredItems.get(request.getItemId());
            listOfRecoveredItems.put(request.getItemId(), qty + request.getQty());
        }
        return listOfRecoveredItems;
    }
}

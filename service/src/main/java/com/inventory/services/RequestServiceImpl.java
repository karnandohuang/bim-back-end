package com.inventory.services;

import com.inventory.models.Request;
import com.inventory.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    RequestRepository requestRepository;


    @Override
    public Request getRequest(String id) {
        return requestRepository.findById(id).get();
    }

    @Override
    @Transactional
    public List<Request> getRequestList() {
        return requestRepository.findAll();
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
    public Map<String, Integer> getRecoveredItems(List<String> ids){
        Map<String, Integer> listOfRecoveredItem = new HashMap<String, Integer>();
        for(String id : ids) {
            Request request = requestRepository.findById(id).get();
            listOfRecoveredItem.put(request.getItemId(), request.getQty());
        }
        return listOfRecoveredItem;
    }
}

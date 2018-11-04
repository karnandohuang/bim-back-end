package com.inventory.services;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    @Transactional
    public Employee getEmployee(String id) {
        return employeeRepository.findById(id).get();
    }

    @Override
    public Employee login(String email, String password) {
        Employee employee;
        try {
            employee = employeeRepository.findByEmail(
                    email);
        } catch (Exception e) {
            return null;
        }
        if (encoder.matches(password, employee.getPassword()))
            return employeeRepository.save(employee);
        else
            return null;
    }


//    @Override
//    @Transactional
//    public List<Employee> getSuperiorList(Paging paging) {
//        List<Employee> list = new ArrayList<>();
//        if(paging.getSortedType().matches("desc"))
//            list = employeeRepository.findAllByDesc(paging.getSortedBy());
//        else
//            list = employeeRepository.findAllByAsc(paging.getSortedBy());
//        long totalRecords = list.size();
//        long offset = ((totalRecords / paging.getPageSize()) * paging.getPageNumber());
//        List<Employee> listOfSuperior = new ArrayList<>();
//        for (long i = (offset+1); i < paging.getPageSize(); i++) {
//            if (list.get((int)i).getSuperiorId().equals("null")) {
//                listOfSuperior.add(list.get((int)i));
//            }
//        }
//        return listOfSuperior;
//    }

    @Override
    @Transactional
    public List<Employee> getSuperiorList(String name, Paging paging) {
        List<Employee> listOfSuperior = new ArrayList<>();
        List<Employee> listOfSortedSuperior = new ArrayList<>();
        if(paging.getSortedType().matches("desc"))
            listOfSuperior = employeeRepository.findAll(new Sort(Sort.Direction.DESC, paging.getSortedBy()));
        else
            listOfSuperior = employeeRepository.findAll(new Sort(Sort.Direction.ASC, paging.getSortedBy()));
        int totalRecords = listOfSuperior.size();
        paging.setTotalRecords(totalRecords);
        int offset = (paging.getPageSize() * (paging.getPageNumber()-1));
        for(int i = 0; i < paging.getPageSize(); i++){
            if((offset + i) >= listOfSortedSuperior.size() || i >= offset) {
                break;
            }
            if (listOfSortedSuperior.get((offset + i)).getSuperiorId().equals("null")) {
                listOfSortedSuperior.add(listOfSortedSuperior.get((offset + i)));
            }
        }
        return listOfSortedSuperior;
    }

//    @Override
//    @Transactional
//    public List<Employee> getEmployeeList(Paging paging) {
//        List<Employee> listOfSortedEmployee = new ArrayList<>();
//        List<Employee> listOfEmployee = new ArrayList<>();
//        if(paging.getSortedType().matches("desc"))
//            listOfEmployee = employeeRepository.findAllByDesc(paging.getSortedBy());
//        else
//            listOfEmployee = employeeRepository.findAllByAsc(paging.getSortedBy());
//        long totalRecords = listOfEmployee.size();
//        long offset = ((totalRecords / paging.getPageSize()) * paging.getPageNumber());
//        for(long i = (offset+1); i < paging.getPageSize(); i++){
//            listOfSortedEmployee.add(listOfEmployee.get((int)i));
//        }
//        return listOfSortedEmployee;
//    }

    @Override
    @Transactional
    public List<Employee> getEmployeeList(String name, Paging paging) {
        List<Employee> listOfSortedEmployee = new ArrayList<>();
        List<Employee> listOfEmployee = new ArrayList<>();
        if(paging.getSortedType().matches("desc")) {
            listOfEmployee = employeeRepository.findAll(new Sort(Sort.Direction.DESC, paging.getSortedBy()));
        }else {
            listOfEmployee = employeeRepository.findAll(new Sort(Sort.Direction.ASC, paging.getSortedBy()));
        }
        int totalRecords = listOfEmployee.size();
        paging.setTotalRecords(totalRecords);
        int offset = (paging.getPageSize() * (paging.getPageNumber()-1));
        for(int i = 0; i < paging.getPageSize(); i++){
            if((offset + i) >= totalRecords || i >= offset) {
                break;
            }
            listOfSortedEmployee.add(listOfEmployee.get((offset + i)));
        }
        return listOfSortedEmployee;
    }

    @Override
    @Transactional
    public Employee saveEmployee(Employee employee) {
        employee.setPassword(encoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public List<String> deleteEmployee(List<String> ids) {
        List<String> listOfNotFoundIds = new ArrayList<>();
        for (String id : ids) {
            try {
                employeeRepository.deleteById(id);
            } catch (NullPointerException e) {
                listOfNotFoundIds.add("id " + id + " not found");
            }
        }
        return listOfNotFoundIds;
    }
}

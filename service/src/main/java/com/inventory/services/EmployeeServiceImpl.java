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

    @Override
    @Transactional
    public List<Employee> getSuperiorList(String name, Paging paging) {
        List<Employee> listOfSuperior = new ArrayList<>();
        List<Employee> listOfEmployee = getEmployeeListFromRepository(name, paging);
        for (Employee em : listOfEmployee) {
            if (em.getSuperiorId().matches("null"))
                listOfSuperior.add(em);
        }
        List<Employee> listOfSortedSuperior = setSortedDataWithPaging(paging, listOfSuperior);
        return listOfSortedSuperior;
    }

    private List<Employee> setSortedDataWithPaging(Paging paging, List<Employee> listOfEmployee) {
        List<Employee> listOfSortedEmployee = new ArrayList<>();
        int offset = paging.getPageSize() * (paging.getPageNumber() - 1);
        int totalRecords = listOfEmployee.size();
        paging.setTotalRecords(totalRecords);
        int totalPage = (int) Math.ceil((float) totalRecords / paging.getPageSize());
        paging.setTotalPage(totalPage);
        for (int i = 0; i < paging.getPageSize(); i++) {
            if ((offset + i) >= listOfEmployee.size())
                break;
            listOfSortedEmployee.add(listOfEmployee.get((offset + i)));
        }
        return listOfSortedEmployee;
    }

    private List<Employee> getEmployeeListFromRepository(String name, Paging paging) {
        List<Employee> listOfEmployee;
        if (paging.getSortedType().matches("desc"))
            listOfEmployee = employeeRepository.findAllByNameContaining(name, new Sort(Sort.Direction.DESC, paging.getSortedBy()));
        else
            listOfEmployee = employeeRepository.findAllByNameContaining(name, new Sort(Sort.Direction.ASC, paging.getSortedBy()));
        return listOfEmployee;
    }

    @Override
    @Transactional
    public List<Employee> getEmployeeList(String name, Paging paging) {
        List<Employee> listOfEmployee = getEmployeeListFromRepository(name, paging);
        List<Employee> listOfSortedEmployee = setSortedDataWithPaging(paging, listOfEmployee);
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

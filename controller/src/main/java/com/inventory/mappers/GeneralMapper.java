package com.inventory.mappers;

import com.inventory.models.Assignment;
import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.webmodels.requests.assignment.AssignmentRequest;
import com.inventory.webmodels.requests.assignment.ChangeAssignmentStatusRequest;
import com.inventory.webmodels.requests.employee.EmployeeRequest;
import com.inventory.webmodels.requests.item.ItemRequest;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

@Component
public class GeneralMapper {

    MapperFactory factory = new DefaultMapperFactory.Builder().build();

    protected void configure() {
        factory.classMap(EmployeeRequest.class, Employee.class)
                .byDefault()
                .mapNulls(true)
                .register();
        factory.classMap(ItemRequest.class, Item.class)
                .byDefault()
                .mapNulls(true)
                .register();
        factory.classMap(AssignmentRequest.class, Assignment.class)
                .byDefault()
                .mapNulls(true)
                .register();
        factory.classMap(ChangeAssignmentStatusRequest.class, Assignment.class)
                .byDefault()
                .mapNulls(true)
                .register();
    }

    public Employee getMappedEmployee(EmployeeRequest request) {
        return factory.getMapperFacade().map(request, Employee.class);
    }

    public Item getMappedItem(ItemRequest request) {
        return factory.getMapperFacade().map(request, Item.class);
    }

    public Assignment getMappedAssignment(AssignmentRequest request) {
        Assignment requestObj = factory.getMapperFacade().map(request, Assignment.class);
        requestObj.setNotes("");
        requestObj.setStatus("Pending");
        return requestObj;
    }

    public Assignment getMappedAssignment(ChangeAssignmentStatusRequest request) {
        Assignment requestObj = factory.getMapperFacade().map(request, Assignment.class);
        if (requestObj.getNotes() == null)
            requestObj.setNotes("");
        return requestObj;
    }
}

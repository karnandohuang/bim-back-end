package com.inventory.mappers;

import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.models.Request;
import com.inventory.webmodels.requests.employee.EmployeeRequest;
import com.inventory.webmodels.requests.item.ItemRequest;
import com.inventory.webmodels.requests.request.ChangeRequestStatusRequest;
import com.inventory.webmodels.requests.request.RequestHTTPRequest;
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
        factory.classMap(RequestHTTPRequest.class, Request.class)
                .byDefault()
                .mapNulls(true)
                .register();
        factory.classMap(ChangeRequestStatusRequest.class, Request.class)
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

    public Request getMappedRequest(RequestHTTPRequest request) {
        Request requestObj = factory.getMapperFacade().map(request, Request.class);
        requestObj.setNotes("");
        requestObj.setStatus("Pending");
        return requestObj;
    }

    public Request getMappedRequest(ChangeRequestStatusRequest request) {
        Request requestObj = factory.getMapperFacade().map(request, Request.class);
        if (requestObj.getNotes() == null)
            requestObj.setNotes("");
        return requestObj;
    }
}

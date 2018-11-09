package com.inventory.mappers;

import com.inventory.models.Employee;
import com.inventory.webmodels.requests.EmployeeRequest;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper extends ConfigurableMapper {

    MapperFactory factory = new DefaultMapperFactory.Builder().build();

    protected void configure() {
        factory.classMap(EmployeeRequest.class, Employee.class)
                .byDefault()
                .mapNulls(true)
                .register();
    }

    public Employee getMappedEmployee(EmployeeRequest request) {
        return factory.getMapperFacade().map(request, Employee.class);
    }
}

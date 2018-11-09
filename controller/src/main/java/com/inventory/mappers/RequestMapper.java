package com.inventory.mappers;

import com.inventory.models.Request;
import com.inventory.webmodels.requests.ChangeRequestStatusRequest;
import com.inventory.webmodels.requests.RequestHTTPRequest;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper extends ConfigurableMapper {

    MapperFactory factory = new DefaultMapperFactory.Builder().build();

    protected void configure() {
        factory.classMap(RequestHTTPRequest.class, Request.class)
                .byDefault()
                .mapNulls(true)
                .register();

        factory.classMap(ChangeRequestStatusRequest.class, Request.class)
                .byDefault()
                .mapNulls(true)
                .register();
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

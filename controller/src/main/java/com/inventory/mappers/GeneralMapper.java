package com.inventory.mappers;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

@Component
public class GeneralMapper {

    MapperFactory factory = new DefaultMapperFactory.Builder().build();

    public <S, C> C map(S source, Class<C> destination) {
        return source == null ? null : factory.getMapperFacade().map(source, destination);
    }
}

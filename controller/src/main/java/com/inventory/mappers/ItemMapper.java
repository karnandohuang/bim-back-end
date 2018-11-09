package com.inventory.mappers;

import com.inventory.models.Item;
import com.inventory.webmodels.requests.ItemRequest;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper extends ConfigurableMapper {

    MapperFactory factory = new DefaultMapperFactory.Builder().build();

    protected void configure() {
        factory.classMap(ItemRequest.class, Item.class)
                .byDefault()
                .mapNulls(true)
                .register();
    }

    public Item getMappedItem(ItemRequest request) {
        return factory.getMapperFacade().map(request, Item.class);
    }
}

package com.lld.urlshortnerservice.common.idgenerator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;


@Component
public class AtomicIdGenerator implements IdGenerator{
    private static final Logger LOGGER= LoggerFactory.getLogger(AtomicIdGenerator.class);
    private final AtomicLong counter;

    public AtomicIdGenerator()
    {
        this.counter=new AtomicLong(1);
    }
    @Override
    public long generateId()
    {
        return counter.getAndIncrement();
    }

}

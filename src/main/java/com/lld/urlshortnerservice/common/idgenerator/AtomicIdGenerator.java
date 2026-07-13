package com.lld.urlshortnerservice.common.idgenerator;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicIdGenerator implements IdGenerator{
    private final AtomicLong counter;

    public AtomicIdGenerator()
    {
        this.counter=new AtomicLong();
    }
    @Override
    public long generateId()
    {
        return counter.getAndIncrement();
    }

}

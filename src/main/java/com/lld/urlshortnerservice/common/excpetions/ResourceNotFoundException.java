package com.lld.urlshortnerservice.common.excpetions;

public class ResourceNotFoundException extends URLException{
    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}

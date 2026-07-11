package com.lld.urlshortnerservice.common.excpetions;

public class InvalidUrlException extends URLException{
    public InvalidUrlException(String message)
    {
        super(message);
    }
}

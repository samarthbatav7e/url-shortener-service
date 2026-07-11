package com.lld.urlshortnerservice.common.excpetions;

public class DuplicateUrlException extends URLException {
    public DuplicateUrlException(String message)
    {
        super(message);
    }
}

package com.lld.urlshortnerservice.common.excpetions;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String message) {
        super(message);
    }
}

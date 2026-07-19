package com.lld.urlshortnerservice.common.excpetions;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;

    private Map<String, String> validationErrors;

    public ErrorResponse()
    {

    }

    public ErrorResponse(
            LocalDateTime timeStamp,
            int status,
            String error,
            String message,
            String path
    )
    {
        this.timeStamp=timeStamp;
        this.status=status;
        this.error=error;
        this.message=message;
        this.path=path;
    }

    public ErrorResponse(
            LocalDateTime timeStamp,
            int status,
            String error,
            String message,
            String path,
            Map<String,String> validationErrors
    )
    {
        this(timeStamp,status,error,message,path);
        this.validationErrors=validationErrors;
    }

}

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
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

}

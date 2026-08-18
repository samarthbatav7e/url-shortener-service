package com.lld.urlshortnerservice.url.mapper;

import com.lld.urlshortnerservice.url.dto.CreateUrlResponse;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public CreateUrlResponse toResponse(UrlMapping mapping)
    {
        return new CreateUrlResponse(
                mapping.getShortCode(),
                baseUrl + "/"+mapping.getShortCode()
        );
    }
}

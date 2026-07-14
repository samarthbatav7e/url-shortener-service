package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.common.excpetions.ResourceNotFoundException;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;

public class UrlRedirectService {
    private final UrlMappingRepository repository;

    public UrlRedirectService(UrlMappingRepository repository)
    {
        this.repository=repository;
    }
    public UrlMapping resolve(String shortCode)
    {
        if(shortCode==null || shortCode.isBlank())
        {
            throw new IllegalArgumentException("Short code cannot be null or blank.");
        }
        return repository.findByShortCode(shortCode)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "No URL found for short code" + shortCode
                ));
    }
}

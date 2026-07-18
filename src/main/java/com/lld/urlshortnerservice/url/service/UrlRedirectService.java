package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.common.excpetions.ResourceNotFoundException;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlRedirectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlRedirectService.class);
    private final UrlMappingRepository repository;

    public UrlRedirectService(UrlMappingRepository repository)
    {
        this.repository=repository;
    }

    public UrlMapping resolve(String shortCode)
    {
         LOGGER.info("Redirect requested for {}", shortCode);

        if(shortCode==null || shortCode.isBlank())
        {
            throw new IllegalArgumentException("Short code cannot be null or blank.");
        }
        Optional<UrlMapping> mapping = repository.findByShortCode(shortCode);

        LOGGER.info(
                "Redirecting {} to {}",
                shortCode,
                mapping.map(UrlMapping::getLongUrl).orElse("NOT_FOUND")
        );
        return  mapping.orElseThrow(
                () -> new ResourceNotFoundException(
                        "No URL found for short code " + shortCode
                ));
    }
}

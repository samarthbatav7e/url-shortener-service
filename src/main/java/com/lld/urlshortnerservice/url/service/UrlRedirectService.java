package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.cache.service.UrlCacheService;
import com.lld.urlshortnerservice.common.excpetions.ResourceNotFoundException;
import com.lld.urlshortnerservice.common.excpetions.UrlExpiredException;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlRedirectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlRedirectService.class);

    private final UrlMappingRepository repository;
    private final UrlCacheService cacheService;

    public UrlRedirectService(UrlMappingRepository repository, UrlCacheService cacheService)
    {
        this.repository=repository;
        this.cacheService=cacheService;
    }

    private UrlMapping validateExpiry(UrlMapping urlMapping)
    {
        LocalDateTime expiry=urlMapping.getExpiresAt();

        if(expiry!=null && expiry.isBefore(LocalDateTime.now()))
        {
            repository.delete(urlMapping);
            cacheService.evict(urlMapping.getShortCode());

           LOGGER.info(
                    "Short URL {} has expired. Removing from database and cache.",
                    urlMapping.getShortCode());

            throw new UrlExpiredException("Short URL " + urlMapping.getShortCode() + " has expired.");
        }

        return urlMapping;
    }

    public UrlMapping resolve(String shortCode)
    {
         LOGGER.info("Resolving Short Code for  {}", shortCode);

         Optional<UrlMapping>cachedMapping=cacheService.getCachedUrlMapping(shortCode);

         if(cachedMapping.isPresent())
         {
             LOGGER.info("Cache hit for Short Code {}", shortCode);
             return  validateExpiry(cachedMapping.get());
         }

         LOGGER.info("Cache miss for Short Code {}", shortCode);

         UrlMapping urlMapping=repository.findByShortCode(shortCode)
                 .orElseThrow(()-> new ResourceNotFoundException(
                         "No URL found for short code " + shortCode
                 ));
          urlMapping=validateExpiry(urlMapping);
         cacheService.cacheUrlMapping(urlMapping);

         LOGGER.info("Cached mapping for Short Code  {}", shortCode);
         return urlMapping;

//        if(shortCode==null || shortCode.isBlank())
//        {
//            throw new IllegalArgumentException("Short code cannot be null or blank.");
//        }
//        Optional<UrlMapping> mapping = repository.findByShortCode(shortCode);
//
//        LOGGER.info(
//                "Redirecting {} to {}",
//                shortCode,
//                mapping.map(UrlMapping::getLongUrl).orElse("NOT_FOUND")
//        );
//        return  mapping.orElseThrow(
//                () -> new ResourceNotFoundException(
//                        "No URL found for short code " + shortCode
//                ));
    }
}

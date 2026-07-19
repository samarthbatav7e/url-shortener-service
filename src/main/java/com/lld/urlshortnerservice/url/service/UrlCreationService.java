package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.cache.service.UrlCacheService;
import com.lld.urlshortnerservice.common.idgenerator.IdGenerator;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;
import com.lld.urlshortnerservice.url.strategy.factory.ShortCodeGeneratorFactory;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlCreationService {

    private static final Logger LOGGER= LoggerFactory.getLogger(UrlCreationService.class);

    private final UrlMappingRepository repository;
    private final UrlCacheService cacheService;
    private final IdGenerator idGenerator;
    private final ShortCodeGeneratorFactory shortCodeGeneratorFactory;

    public UrlCreationService(UrlMappingRepository repository, UrlCacheService cacheService, IdGenerator idGenerator, ShortCodeGeneratorFactory shortCodegeneratorFactory)
    {
        this.repository=repository;
        this.cacheService=cacheService;
        this.idGenerator=idGenerator;
        this.shortCodeGeneratorFactory=shortCodegeneratorFactory;
    }

    private void validateRequest(CreateUrlRequest request)
    {
        if(request==null)
        {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if(request.getLongUrl()==null || request.getLongUrl().isBlank())
        {
            throw new IllegalArgumentException("Long URL cannot be empty.");
        }
        if(request.getGenerationStrategy()==null)
        {
            throw new IllegalArgumentException("Short code generation strategy cannot be null.");
        }
    }

    public UrlMapping createShortUrl(CreateUrlRequest request)
    {

        //validating if a request is proper and legit as per basic standards
        validateRequest(request);

        // checking if a URLMapping for the long URL already exists, if yes then returning it
        Optional<UrlMapping>existingMapping=repository.findByLongUrl(request.getLongUrl());
        if(existingMapping.isPresent())
        {
            LOGGER.info("URL already exists, returning existing short code {}",
                    existingMapping.get().getShortCode());

            return existingMapping.get();
        }

        //create a fresh UrlMapping for the long URL if the mapping already doesn't exist

        // generating a unique numeric id
        long id= idGenerator.generateId();
        LOGGER.debug("Generated ID {}", id);

        //selecting the short code generator
        ShortCodeGenerator generator=shortCodeGeneratorFactory.getGenerator(request.getGenerationStrategy());

        //generating short code
        String shortCode=generator.generate(id);
        LOGGER.debug("Generated short code {} using strategy {}",
                shortCode, request.getGenerationStrategy());

        LOGGER.info("Creating short URL for {} ", request.getLongUrl());

        //creating url mapping object
        UrlMapping urlMapping=
                new UrlMapping.Builder()
                        .id(id)
                        .longUrl(request.getLongUrl())
                        .shortCode(shortCode)
                        .generationStrategy(request.getGenerationStrategy())
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        //persisting the UrlMapping in repository
       UrlMapping savedMapping = repository.save(urlMapping);
        cacheService.cacheUrlMapping(savedMapping);

        LOGGER.info("Cached mapping for short code {}", savedMapping.getShortCode());

        LOGGER.info("Created short URL {} for {}", savedMapping.getShortCode(), savedMapping.getLongUrl());

        //returning the urlMapping

        return urlMapping;
    }






}

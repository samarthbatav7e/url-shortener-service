package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.common.idgenerator.IdGenerator;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.dto.CreateUrlResponse;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;
import com.lld.urlshortnerservice.url.strategy.factory.ShortCodeGeneratorFactory;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;

import java.time.LocalDateTime;
import java.util.Optional;

public class UrlCreationService {
    private final UrlMappingRepository repository;
    private final IdGenerator idGenerator;
    private final ShortCodeGeneratorFactory shortCodeGeneratorFactory;

    public UrlCreationService(UrlMappingRepository repository, IdGenerator idGenerator, ShortCodeGeneratorFactory shortCodegeneratorFactory)
    {
        this.repository=repository;
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
            return existingMapping.get();
        }

        //create a fresh UrlMapping for the long URL if the mapping already doesn't exist

        // generating a unique numeric id
        long id= idGenerator.generateId();

        //selecting the short code generator
        ShortCodeGenerator generator=shortCodeGeneratorFactory.getGenerator(request.getGenerationStrategy());

        //generating short code
        String shortCode=generator.generate(id);

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
        repository.save(urlMapping);

        //returning the urlMapping

        return urlMapping;
    }






}

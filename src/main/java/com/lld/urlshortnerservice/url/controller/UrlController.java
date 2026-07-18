package com.lld.urlshortnerservice.url.controller;

import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.dto.CreateUrlResponse;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.mapper.UrlMapper;
import com.lld.urlshortnerservice.url.service.UrlCreationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/urls")
public class UrlController {
    private final UrlCreationService creationService;
    private final UrlMapper urlMapper;

    public UrlController(UrlCreationService service, UrlMapper mapper)
    {
        this.creationService=service;
        this.urlMapper=mapper;
    }

    @PostMapping
    public ResponseEntity<CreateUrlResponse> createShortUrl(@RequestBody CreateUrlRequest request)
    {
        UrlMapping mapping=creationService.createShortUrl(request);
        CreateUrlResponse response=urlMapper.toResponse(mapping);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


}

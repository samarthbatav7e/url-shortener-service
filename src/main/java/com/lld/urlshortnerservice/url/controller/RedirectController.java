package com.lld.urlshortnerservice.url.controller;

import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.mapper.UrlMapper;
import com.lld.urlshortnerservice.url.service.UrlRedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirectController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectController.class);

    private final UrlRedirectService redirectService;

    public RedirectController(UrlRedirectService service)
    {
        this.redirectService=service;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode)
    {
        LOGGER.info("GET/ {}", shortCode);

        UrlMapping mapping=redirectService.resolve(shortCode);

        HttpHeaders headers=new HttpHeaders();
        headers.setLocation(URI.create(mapping.getLongUrl()));

        return new ResponseEntity<>(
                headers,
                HttpStatus.FOUND
        );

    }

}

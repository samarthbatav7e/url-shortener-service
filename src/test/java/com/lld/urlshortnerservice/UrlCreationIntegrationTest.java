package com.lld.urlshortnerservice;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.service.UrlCreationService;
import com.lld.urlshortnerservice.url.service.UrlRedirectService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class UrlCreationIntegrationTest {

    @Autowired
    private UrlCreationService creationService;

    @Autowired
    private UrlRedirectService redirectService;

    @Test
    void shouldCreateAndResolveUrl() {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://www.google.com",
                        CodeGenerationStrategy.BASE62);

        UrlMapping created =
                creationService.createShortUrl(request);

        UrlMapping resolved =
                redirectService.resolve(
                        created.getShortCode());

        assertEquals(
                created.getLongUrl(),
                resolved.getLongUrl());
    }
}
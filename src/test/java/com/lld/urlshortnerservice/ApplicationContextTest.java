package com.lld.urlshortnerservice;

import com.lld.urlshortnerservice.url.service.UrlCreationService;
import com.lld.urlshortnerservice.url.service.UrlRedirectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ApplicationContextTest {
    @Autowired
    private UrlCreationService creationService;

    @Autowired
    private UrlRedirectService redirectService;

    @Test
    void contextLoads()
    {
        assertNotNull(creationService);
        assertNotNull(redirectService);
    }

}

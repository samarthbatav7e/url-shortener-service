package com.lld.urlshortnerservice;
import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.excpetions.ResourceNotFoundException;
import com.lld.urlshortnerservice.common.idgenerator.AtomicIdGenerator;
import com.lld.urlshortnerservice.common.idgenerator.IdGenerator;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.InMemoryUrlMappingRepository;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;
import com.lld.urlshortnerservice.url.service.UrlCreationService;
import com.lld.urlshortnerservice.url.service.UrlRedirectService;
import com.lld.urlshortnerservice.url.strategy.factory.ShortCodeGeneratorFactory;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;
import com.lld.urlshortnerservice.url.strategy.impl.Base62Generator;
import com.lld.urlshortnerservice.url.strategy.impl.SequentialGenerator;


import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UrlRedirectServiceTest {
    @Test
    void shouldResolveShortCodeSuccessfully() {

        UrlMappingRepository repository =
                new InMemoryUrlMappingRepository();

        IdGenerator idGenerator =
                new AtomicIdGenerator();

        ShortCodeGeneratorFactory factory =
                new ShortCodeGeneratorFactory(
                        List.of(
                                new Base62Generator(),
                                new SequentialGenerator()));

        UrlCreationService creationService =
                new UrlCreationService(
                        repository,
                        idGenerator,
                        factory);

        UrlRedirectService redirectService =
                new UrlRedirectService(
                        repository);

        UrlMapping created =
                creationService.createShortUrl(
                        new CreateUrlRequest(
                                "https://www.google.com",
                                CodeGenerationStrategy.BASE62));

        UrlMapping resolved =
                redirectService.resolve(
                        created.getShortCode());

        assertEquals(
                created.getLongUrl(),
                resolved.getLongUrl());

        assertEquals(
                created.getShortCode(),
                resolved.getShortCode());

        System.out.println("Created URL: "+ created);
        System.out.println("Resolved short code: " + resolved);
    }


    @Test
    void shouldThrowExceptionForUnknownShortCode() {

        UrlMappingRepository repository =
                new InMemoryUrlMappingRepository();

        UrlRedirectService service =
                new UrlRedirectService(repository);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.resolve("XYZ123"));

//        UrlRedirectService redirectService =
//                new UrlRedirectService(
//                        repository);
//        UrlMapping resolved =
//                redirectService.resolve("XYZ123");
//
//        System.out.println("Resolved short code: " + resolved);
    }

    @Test
    void shouldRejectBlankShortCode() {

        UrlMappingRepository repository =
                new InMemoryUrlMappingRepository();

        UrlRedirectService service =
                new UrlRedirectService(repository);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.resolve(""));
    }
}

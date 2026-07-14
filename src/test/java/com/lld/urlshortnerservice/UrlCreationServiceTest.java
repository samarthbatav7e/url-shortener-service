package com.lld.urlshortnerservice;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.idgenerator.AtomicIdGenerator;
import com.lld.urlshortnerservice.common.idgenerator.IdGenerator;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.InMemoryUrlMappingRepository;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;
import com.lld.urlshortnerservice.url.service.UrlCreationService;
import com.lld.urlshortnerservice.url.strategy.factory.ShortCodeGeneratorFactory;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;
import com.lld.urlshortnerservice.url.strategy.impl.Base62Generator;
import com.lld.urlshortnerservice.url.strategy.impl.SequentialGenerator;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class UrlCreationServiceTest {
    @Test
    void shouldCreateShortUrlSuccessfully() {

        /*
         * Arrange
         */

        UrlMappingRepository repository =
                new InMemoryUrlMappingRepository();

        IdGenerator idGenerator =
                new AtomicIdGenerator();

        List<ShortCodeGenerator> generators = List.of(
                new Base62Generator(),
                new SequentialGenerator()
        );

        ShortCodeGeneratorFactory factory =
                new ShortCodeGeneratorFactory(generators);

        UrlCreationService service =
                new UrlCreationService(
                        repository,
                        idGenerator,
                        factory
                );

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://www.google.com",
                        CodeGenerationStrategy.BASE62
                );

        /*
         * Act
         */

        UrlMapping result =
                service.createShortUrl(request);

        /*
         * Assert
         */

        assertNotNull(result);

        assertEquals(
                "https://www.google.com",
                result.getLongUrl());

        assertNotNull(result.getShortCode());

        System.out.println(result);
    }



    @Test
    void shouldReturnExistingMappingWhenUrlAlreadyExists() {

        UrlMappingRepository repository =
                new InMemoryUrlMappingRepository();

        IdGenerator idGenerator =
                new AtomicIdGenerator();

        ShortCodeGeneratorFactory factory =
                new ShortCodeGeneratorFactory(
                        List.of(
                                new Base62Generator(),
                                new SequentialGenerator()));

        UrlCreationService service =
                new UrlCreationService(
                        repository,
                        idGenerator,
                        factory);

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://www.google.com",
                        CodeGenerationStrategy.BASE62);

        UrlMapping first =
                service.createShortUrl(request);

        UrlMapping second =
                service.createShortUrl(request);

        assertEquals(
                first.getShortCode(),
                second.getShortCode());

        assertEquals(
                first.getId(),
                second.getId());
    }

    @Test
    void shouldGenerateDifferentShortCodesForDifferentStrategies() {

        UrlMappingRepository repository =
                new InMemoryUrlMappingRepository();

        IdGenerator idGenerator =
                new AtomicIdGenerator();

        ShortCodeGeneratorFactory factory =
                new ShortCodeGeneratorFactory(
                        List.of(
                                new Base62Generator(),
                                new SequentialGenerator()));

        UrlCreationService service =
                new UrlCreationService(
                        repository,
                        idGenerator,
                        factory);

        UrlMapping base62 =
                service.createShortUrl(
                        new CreateUrlRequest(
                                "https://google.com",
                                CodeGenerationStrategy.BASE62));

        UrlMapping sequential =
                service.createShortUrl(
                        new CreateUrlRequest(
                                "https://stackoverflow.com",
                                CodeGenerationStrategy.SEQUENTIAL));

        System.out.println(base62);

        System.out.println(sequential);

        assertNotEquals(
                base62.getShortCode(),
                sequential.getShortCode());
    }
    @Test
    void shouldFindUrlByShortCode() {

        UrlMappingRepository repository =
                new InMemoryUrlMappingRepository();

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(1L)
                        .longUrl("https://google.com")
                        .shortCode("1")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .build();

        repository.save(mapping);

        assertTrue(
                repository.findByShortCode("1").isPresent());

        assertTrue(
                repository.findByLongUrl("https://google.com").isPresent());
    }
}

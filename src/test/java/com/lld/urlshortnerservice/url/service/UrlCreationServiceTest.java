package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.idgenerator.IdGenerator;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;
import com.lld.urlshortnerservice.url.strategy.factory.ShortCodeGeneratorFactory;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlCreationServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ShortCodeGeneratorFactory generatorFactory;

    @Mock
    private ShortCodeGenerator generator;

    @InjectMocks
    private UrlCreationService creationService;

    private CreateUrlRequest request;

    @BeforeEach
    void setUp() {

        request = new CreateUrlRequest(
                "https://www.google.com",
                CodeGenerationStrategy.BASE62
        );
    }

    @Test
    @DisplayName("Should create new short URL")
    void shouldCreateShortUrl() {

        when(repository.findByLongUrl(request.getLongUrl()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(1L);

        when(generatorFactory.getGenerator(CodeGenerationStrategy.BASE62))
                .thenReturn(generator);

        when(generator.generate(1L))
                .thenReturn("1");

        UrlMapping result =
                creationService.createShortUrl(request);

        assertNotNull(result);

        assertEquals(
                "https://www.google.com",
                result.getLongUrl());

        assertEquals(
                "1",
                result.getShortCode());

        assertEquals(
                CodeGenerationStrategy.BASE62,
                result.getGenerationStrategy());

        verify(repository, times(1))
                .save(any(UrlMapping.class));

        verify(idGenerator)
                .generateId();

        verify(generatorFactory)
                .getGenerator(CodeGenerationStrategy.BASE62);

        verify(generator)
                .generate(1L);
    }

    @Test
    @DisplayName("Should return existing mapping")
    void shouldReturnExistingMapping() {

        UrlMapping existing =
                new UrlMapping.Builder()
                        .id(10L)
                        .longUrl(request.getLongUrl())
                        .shortCode("ABC")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .build();

        when(repository.findByLongUrl(request.getLongUrl()))
                .thenReturn(Optional.of(existing));

        UrlMapping result =
                creationService.createShortUrl(request);

        assertSame(existing, result);

        verify(repository, never())
                .save(any());

        verify(idGenerator, never())
                .generateId();

        verify(generatorFactory, never())
                .getGenerator(any());

        verify(generator, never())
                .generate(anyLong());
    }

    @Test
    @DisplayName("Should throw when request is null")
    void shouldThrowWhenRequestIsNull() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creationService.createShortUrl(null)
                );

        assertEquals(
                "Request cannot be null",
                exception.getMessage());

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should throw when URL is blank")
    void shouldThrowWhenUrlIsBlank() {

        CreateUrlRequest invalid =
                new CreateUrlRequest(
                        "",
                        CodeGenerationStrategy.BASE62
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creationService.createShortUrl(invalid)
                );

        assertEquals(
                "Long URL cannot be empty.",
                exception.getMessage());

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should throw when strategy is null")
    void shouldThrowWhenStrategyIsNull() {

        CreateUrlRequest invalid =
                new CreateUrlRequest(
                        "https://google.com",
                        null
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> creationService.createShortUrl(invalid)
                );

        assertEquals(
                "Short code generation strategy cannot be null.",
                exception.getMessage());

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should save correct UrlMapping")
    void shouldSaveCorrectUrlMapping() {

        when(repository.findByLongUrl(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(99L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("XYZ99");

        creationService.createShortUrl(request);

        ArgumentCaptor<UrlMapping> captor =
                ArgumentCaptor.forClass(UrlMapping.class);

        verify(repository)
                .save(captor.capture());

        UrlMapping saved =
                captor.getValue();

        assertEquals(
                99L,
                saved.getId());

        assertEquals(
                "XYZ99",
                saved.getShortCode());

        assertEquals(
                request.getLongUrl(),
                saved.getLongUrl());

        assertEquals(
                CodeGenerationStrategy.BASE62,
                saved.getGenerationStrategy());

        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Should generate ID exactly once")
    void shouldGenerateIdOnlyOnce() {

        when(repository.findByLongUrl(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(7L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("7");

        creationService.createShortUrl(request);

        verify(idGenerator, times(1))
                .generateId();
    }

    @Test
    @DisplayName("Should use BASE62 generator")
    void shouldUseBase62Generator() {

        when(repository.findByLongUrl(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(15L);

        when(generatorFactory.getGenerator(CodeGenerationStrategy.BASE62))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("F");

        creationService.createShortUrl(request);

        verify(generatorFactory)
                .getGenerator(CodeGenerationStrategy.BASE62);
    }

    @Test
    @DisplayName("Should invoke repository lookup once")
    void shouldLookupRepositoryOnlyOnce() {

        when(repository.findByLongUrl(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(1L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("1");

        creationService.createShortUrl(request);

        verify(repository, times(1))
                .findByLongUrl(request.getLongUrl());
    }
}
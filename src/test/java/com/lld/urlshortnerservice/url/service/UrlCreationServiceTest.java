package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.cache.service.UrlCacheService;
import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.idgenerator.IdGenerator;
import com.lld.urlshortnerservice.common.util.HashUtils;
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
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
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

    @Mock
    private UrlCacheService cacheService;

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

        when(repository.findByLongUrlHash(HashUtils.sha256Hex(request.getLongUrl())))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(1L);

        when(generatorFactory.getGenerator(CodeGenerationStrategy.BASE62))
                .thenReturn(generator);

        when(generator.generate(1L))
                .thenReturn("1");
        when(repository.save(any(UrlMapping.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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
        verify(cacheService)
                .cacheUrlMapping(any(UrlMapping.class));
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

        when(repository.findByLongUrlHash(HashUtils.sha256Hex(request.getLongUrl())))
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

        verify(cacheService, never())
                .cacheUrlMapping(any());
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

        when(repository.findByLongUrlHash(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(99L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("XYZ99");
        when(repository.save(any(UrlMapping.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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
        assertNull(saved.getExpiresAt());
    }

    @Test
    @DisplayName("Should generate ID exactly once")
    void shouldGenerateIdOnlyOnce() {

        when(repository.findByLongUrlHash(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(7L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("7");
        when(repository.save(any(UrlMapping.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        creationService.createShortUrl(request);

        verify(idGenerator, times(1))
                .generateId();
    }

    @Test
    @DisplayName("Should use BASE62 generator")
    void shouldUseBase62Generator() {

        when(repository.findByLongUrlHash(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(15L);

        when(generatorFactory.getGenerator(CodeGenerationStrategy.BASE62))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("F");
        when(repository.save(any(UrlMapping.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        creationService.createShortUrl(request);

        verify(generatorFactory)
                .getGenerator(CodeGenerationStrategy.BASE62);
    }

    @Test
    @DisplayName("Should invoke repository lookup once")
    void shouldLookupRepositoryOnlyOnce() {

        when(repository.findByLongUrlHash(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(1L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("1");
        when(repository.save(any(UrlMapping.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        creationService.createShortUrl(request);

        verify(repository, times(1))
                .findByLongUrlHash(HashUtils.sha256Hex(request.getLongUrl()));
    }
    @Test
    @DisplayName("Should save expiry date when provided")
    void shouldSaveExpiryDate() {

        LocalDateTime expiry =
                LocalDateTime.now().plusDays(1);

        request.setExpiresAt(expiry);

        when(repository.findByLongUrlHash(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(10L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("ABC");

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        creationService.createShortUrl(request);

        ArgumentCaptor<UrlMapping> captor =
                ArgumentCaptor.forClass(UrlMapping.class);

        verify(repository).save(captor.capture());

        assertEquals(
                expiry,
                captor.getValue().getExpiresAt());
    }

    @Test
    @DisplayName("Should return existing mapping when a concurrent request wins the insert")
    void shouldReturnWinnerWhenInsertLosesRace() {

        String hash = HashUtils.sha256Hex(request.getLongUrl());

        UrlMapping winner =
                new UrlMapping.Builder()
                        .id(5L)
                        .longUrl(request.getLongUrl())
                        .shortCode("W")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .build();

        when(repository.findByLongUrlHash(hash))
                .thenReturn(Optional.empty(), Optional.of(winner));

        when(idGenerator.generateId())
                .thenReturn(6L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("L");

        when(repository.save(any(UrlMapping.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        UrlMapping result =
                creationService.createShortUrl(request);

        assertSame(winner, result);

        verify(repository, times(2))
                .findByLongUrlHash(hash);

        verify(cacheService, never())
                .cacheUrlMapping(any());
    }

    @Test
    @DisplayName("Should rethrow when insert fails and no mapping exists")
    void shouldRethrowWhenViolationHasNoWinner() {

        when(repository.findByLongUrlHash(any()))
                .thenReturn(Optional.empty());

        when(idGenerator.generateId())
                .thenReturn(6L);

        when(generatorFactory.getGenerator(any()))
                .thenReturn(generator);

        when(generator.generate(anyLong()))
                .thenReturn("L");

        when(repository.save(any(UrlMapping.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> creationService.createShortUrl(request));

        verify(cacheService, never())
                .cacheUrlMapping(any());
    }

    @Test
    @DisplayName("Should look up existing mapping by the SHA-256 hash of the long URL")
    void shouldLookUpByHash() {

        String expectedHash = HashUtils.sha256Hex(request.getLongUrl());

        UrlMapping existing =
                new UrlMapping.Builder()
                        .id(10L)
                        .longUrl(request.getLongUrl())
                        .shortCode("ABC")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .build();

        when(repository.findByLongUrlHash(expectedHash))
                .thenReturn(Optional.of(existing));

        assertSame(existing, creationService.createShortUrl(request));

        verify(repository).findByLongUrlHash(expectedHash);
        verify(repository, never()).findByLongUrl(any());
    }
}
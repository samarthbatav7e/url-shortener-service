package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.cache.service.UrlCacheService;
import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.excpetions.ResourceNotFoundException;
import com.lld.urlshortnerservice.common.excpetions.UrlExpiredException;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlRedirectServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @InjectMocks
    private UrlRedirectService redirectService;

    private UrlMapping mapping;

    @Mock
    private UrlCacheService cacheService;

    @BeforeEach
    void setUp() {

        mapping =
                new UrlMapping.Builder()
                        .id(1L)
                        .longUrl("https://www.google.com")
                        .shortCode("abc123")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();
    }

    @Test
    @DisplayName("Should resolve short code successfully")
    void shouldResolveShortCodeSuccessfully() {

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.empty());

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(mapping));

        UrlMapping result =
                redirectService.resolve("abc123");

        assertNotNull(result);

        assertEquals(
                "https://www.google.com",
                result.getLongUrl());

        assertEquals(
                "abc123",
                result.getShortCode());
        verify(cacheService)
                .getCachedUrlMapping("abc123");

        verify(repository, times(1))
                .findByShortCode("abc123");

        verify(cacheService)
                .cacheUrlMapping(mapping);

        verifyNoMoreInteractions(repository);
    }


    @Test
    @DisplayName("Should throw ResourceNotFoundException when mapping is absent")
    void shouldThrowResourceNotFoundException() {

        when(repository.findByShortCode("missing"))
                .thenReturn(Optional.empty());

        when(cacheService.getCachedUrlMapping("missing"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> redirectService.resolve("missing")
                );

        assertEquals(
                "No URL found for short code missing",
                exception.getMessage());

        verify(repository)
                .findByShortCode("missing");

        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should lookup repository exactly once")
    void shouldLookupRepositoryExactlyOnce() {

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.empty());

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(mapping));

        redirectService.resolve("abc123");

        verify(repository, times(1))
                .findByShortCode("abc123");
    }

    @Test
    @DisplayName("Should return exact mapping from repository")
    void shouldReturnExactMappingFromRepository() {

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.empty());

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(mapping));

        UrlMapping result =
                redirectService.resolve("abc123");

        assertSame(mapping, result);
    }

    @Test
    @DisplayName("Should preserve all UrlMapping fields")
    void shouldPreserveAllFields() {

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(mapping));
        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.empty());

        UrlMapping result =
                redirectService.resolve("abc123");

        assertAll(

                () -> assertEquals(
                        mapping.getId(),
                        result.getId()),

                () -> assertEquals(
                        mapping.getLongUrl(),
                        result.getLongUrl()),

                () -> assertEquals(
                        mapping.getShortCode(),
                        result.getShortCode()),

                () -> assertEquals(
                        mapping.getGenerationStrategy(),
                        result.getGenerationStrategy()),

                () -> assertEquals(
                        mapping.getCreatedAt(),
                        result.getCreatedAt()),

                () -> assertEquals(
                        mapping.getExpiresAt(),
                        result.getExpiresAt())
        );
    }

    @Test
    @DisplayName("Should return cached mapping when cache hit occurs")
    void shouldReturnCachedMappingWhenPresent() {

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.of(mapping));

        UrlMapping result =
                redirectService.resolve("abc123");

        assertSame(mapping, result);

        verifyNoInteractions(repository);

        verify(cacheService)
                .getCachedUrlMapping("abc123");

        verify(cacheService, never())
                .cacheUrlMapping(any());
    }
    @Test
    @DisplayName("Should query repository and cache mapping when cache misses")
    void shouldQueryRepositoryAndCacheMappingWhenCacheMisses() {

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.empty());

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(mapping));

        UrlMapping result =
                redirectService.resolve("abc123");

        assertSame(mapping, result);

        verify(cacheService)
                .getCachedUrlMapping("abc123");

        verify(repository)
                .findByShortCode("abc123");

        verify(cacheService)
                .cacheUrlMapping(mapping);
    }
    @Test
    @DisplayName("Should throw when mapping not found in cache or repository")
    void shouldThrowWhenCacheAndRepositoryMiss() {

        when(cacheService.getCachedUrlMapping("missing"))
                .thenReturn(Optional.empty());

        when(repository.findByShortCode("missing"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> redirectService.resolve("missing")
                );

        assertEquals(
                "No URL found for short code missing",
                exception.getMessage());

        verify(cacheService)
                .getCachedUrlMapping("missing");

        verify(repository)
                .findByShortCode("missing");

        verify(cacheService, never())
                .cacheUrlMapping(any());
    }

    @Test
    @DisplayName("Should evict cache, delete mapping and throw when cached URL is expired")
    void shouldThrowWhenCachedUrlIsExpired() {

        UrlMapping expired =
                new UrlMapping.Builder()
                        .id(1L)
                        .longUrl("https://google.com")
                        .shortCode("abc123")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .expiresAt(LocalDateTime.now().minusMinutes(1))
                        .build();

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.of(expired));

        UrlExpiredException exception =
                assertThrows(
                        UrlExpiredException.class,
                        () -> redirectService.resolve("abc123")
                );

        assertEquals(
                "Short URL " + expired.getShortCode() + " has expired.",
                exception.getMessage());

        verify(repository, never())
                .findByShortCode(any());

        verify(repository)
                .delete(expired);

        verify(cacheService)
                .evict("abc123");

        verify(cacheService, never())
                .cacheUrlMapping(any());
    }

    @Test
    @DisplayName("Should delete expired database mapping and throw exception")
    void shouldThrowWhenDatabaseUrlIsExpired() {

        UrlMapping expired =
                new UrlMapping.Builder()
                        .id(1L)
                        .longUrl("https://google.com")
                        .shortCode("abc123")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .expiresAt(LocalDateTime.now().minusMinutes(1))
                        .build();

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.empty());

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(expired));

        UrlExpiredException exception =
                assertThrows(
                        UrlExpiredException.class,
                        () -> redirectService.resolve("abc123")
                );

        assertEquals(
                "Short URL " + expired.getShortCode() + " has expired.",
                exception.getMessage());

        verify(repository)
                .findByShortCode("abc123");

        verify(repository)
                .delete(expired);

        verify(cacheService)
                .evict("abc123");

        verify(cacheService, never())
                .cacheUrlMapping(any());
    }

    @Test
    @DisplayName("Should resolve URL when expiry is null")
    void shouldResolveWhenExpiryIsNull() {

        mapping.setExpiresAt(null);

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.of(mapping));

        UrlMapping result =
                redirectService.resolve("abc123");

        assertSame(mapping, result);

        verify(repository, never())
                .findByShortCode(any());

        verify(repository, never())
                .delete(any());

        verify(cacheService, never())
                .evict(any());
    }

    @Test
    @DisplayName("Should resolve URL when expiry is in future")
    void shouldResolveWhenExpiryIsInFuture() {

        mapping.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(cacheService.getCachedUrlMapping("abc123"))
                .thenReturn(Optional.of(mapping));

        UrlMapping result =
                redirectService.resolve("abc123");

        assertSame(mapping, result);

        verify(repository, never())
                .delete(any());

        verify(cacheService, never())
                .evict(any());
    }
}
package com.lld.urlshortnerservice.url.service;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.excpetions.ResourceNotFoundException;
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

        verify(repository, times(1))
                .findByShortCode("abc123");

        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when short code is null")
    void shouldThrowExceptionWhenShortCodeIsNull() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> redirectService.resolve(null)
                );

        assertEquals(
                "Short code cannot be null or blank.",
                exception.getMessage());

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when short code is blank")
    void shouldThrowExceptionWhenShortCodeIsBlank() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> redirectService.resolve("")
                );

        assertEquals(
                "Short code cannot be null or blank.",
                exception.getMessage());

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when mapping is absent")
    void shouldThrowResourceNotFoundException() {

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

        verify(repository)
                .findByShortCode("missing");

        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should lookup repository exactly once")
    void shouldLookupRepositoryExactlyOnce() {

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(mapping));

        redirectService.resolve("abc123");

        verify(repository, times(1))
                .findByShortCode("abc123");
    }

    @Test
    @DisplayName("Should return exact mapping from repository")
    void shouldReturnExactMappingFromRepository() {

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
    @DisplayName("Should not call repository for blank short code")
    void shouldNotCallRepositoryForBlankInput() {

        assertThrows(
                IllegalArgumentException.class,
                () -> redirectService.resolve(" ")
        );

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should not call repository for null input")
    void shouldNotCallRepositoryForNullInput() {

        assertThrows(
                IllegalArgumentException.class,
                () -> redirectService.resolve(null)
        );

        verifyNoInteractions(repository);
    }
}
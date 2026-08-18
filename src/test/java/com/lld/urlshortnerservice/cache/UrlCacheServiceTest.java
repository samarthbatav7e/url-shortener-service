package com.lld.urlshortnerservice.cache;

import com.lld.urlshortnerservice.cache.repository.UrlCacheRepository;
import com.lld.urlshortnerservice.cache.service.UrlCacheService;
import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheServiceTest {

    @Mock
    private UrlCacheRepository repository;

    @InjectMocks
    private UrlCacheService cacheService;

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
                        .build();
    }

    @Test
    @DisplayName("Should return cached mapping")
    void shouldReturnCachedMapping() {

        when(repository.get("abc123"))
                .thenReturn(Optional.of(mapping));

        Optional<UrlMapping> result =
                cacheService.getCachedUrlMapping("abc123");

        assertTrue(result.isPresent());
        assertSame(mapping, result.get());

        verify(repository)
                .get("abc123");
    }

    @Test
    @DisplayName("Should cache UrlMapping")
    void shouldCacheUrlMapping() {

        cacheService.cacheUrlMapping(mapping);

        verify(repository)
                .save(mapping);
    }

    @Test
    @DisplayName("Should evict cached UrlMapping")
    void shouldEvictCachedMapping() {

        cacheService.evict("abc123");

        verify(repository)
                .delete("abc123");
    }
}
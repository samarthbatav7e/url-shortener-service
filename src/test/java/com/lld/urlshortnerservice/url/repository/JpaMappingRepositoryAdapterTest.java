package com.lld.urlshortnerservice.url.repository;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUrlMappingRepositoryAdapterTest {

    @Mock
    private JpaUrlMappingRepository jpaRepository;

    @InjectMocks
    private JpaUrlMappingRepositoryAdapter adapter;

    private UrlMapping mapping;

    @BeforeEach
    void setUp() {

        mapping =
                new UrlMapping.Builder()
                        .id(1L)
                        .longUrl("https://google.com")
                        .shortCode("abc123")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .build();
    }

    @Test
    @DisplayName("Should save UrlMapping")
    void shouldSaveUrlMapping() {

        when(jpaRepository.save(mapping))
                .thenReturn(mapping);

        UrlMapping result = adapter.save(mapping);

        assertSame(mapping, result);

        verify(jpaRepository)
                .save(mapping);
    }

    @Test
    @DisplayName("Should find by long URL")
    void shouldFindByLongUrl() {

        when(jpaRepository.findByLongUrl(mapping.getLongUrl()))
                .thenReturn(Optional.of(mapping));

        Optional<UrlMapping> result =
                adapter.findByLongUrl(mapping.getLongUrl());

        assertTrue(result.isPresent());

        assertSame(mapping, result.get());

        verify(jpaRepository)
                .findByLongUrl(mapping.getLongUrl());
    }

    @Test
    @DisplayName("Should find by short code")
    void shouldFindByShortCode() {

        when(jpaRepository.findByShortCode(mapping.getShortCode()))
                .thenReturn(Optional.of(mapping));

        Optional<UrlMapping> result =
                adapter.findByShortCode(mapping.getShortCode());

        assertTrue(result.isPresent());

        assertSame(mapping, result.get());

        verify(jpaRepository)
                .findByShortCode(mapping.getShortCode());
    }

    @Test
    @DisplayName("Should check short code existence")
    void shouldCheckShortCodeExists() {

        when(jpaRepository.existsByShortCode("abc123"))
                .thenReturn(true);

        assertTrue(adapter.existsByShortCode("abc123"));

        verify(jpaRepository)
                .existsByShortCode("abc123");
    }

    @Test
    @DisplayName("Should check long URL existence")
    void shouldCheckLongUrlExists() {

        when(jpaRepository.existsByLongUrl(mapping.getLongUrl()))
                .thenReturn(true);

        assertTrue(adapter.existsByLongUrl(mapping.getLongUrl()));

        verify(jpaRepository)
                .existsByLongUrl(mapping.getLongUrl());
    }

    @Test
    @DisplayName("Should delete UrlMapping")
    void shouldDeleteUrlMapping() {

        adapter.delete(mapping);

        verify(jpaRepository)
                .delete(mapping);
    }
}
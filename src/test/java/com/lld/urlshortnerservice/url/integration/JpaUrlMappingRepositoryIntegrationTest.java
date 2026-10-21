package com.lld.urlshortnerservice.url.integration;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.entity.UrlMapping;

import com.lld.urlshortnerservice.url.repository.JpaUrlMappingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaUrlMappingRepositoryIntegrationTest {

    @Autowired
    private JpaUrlMappingRepository repository;

    private UrlMapping createMapping() {

        return new UrlMapping.Builder()
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

        UrlMapping saved = repository.save(createMapping());

        assertNotNull(saved);
        assertEquals("abc123", saved.getShortCode());
    }

    @Test
    @DisplayName("Should find by short code")
    void shouldFindByShortCode() {

        repository.save(createMapping());

        Optional<UrlMapping> result =
                repository.findByShortCode("abc123");

        assertTrue(result.isPresent());
        assertEquals(
                "https://google.com",
                result.get().getLongUrl());
    }

    @Test
    @DisplayName("Should find by long URL")
    void shouldFindByLongUrl() {

        repository.save(createMapping());

        Optional<UrlMapping> result =
                repository.findByLongUrl("https://google.com");

        assertTrue(result.isPresent());
        assertEquals(
                "abc123",
                result.get().getShortCode());
    }

    @Test
    @DisplayName("Should check short code exists")
    void shouldCheckShortCodeExists() {

        repository.save(createMapping());

        assertTrue(repository.existsByShortCode("abc123"));
    }

    @Test
    @DisplayName("Should check long URL exists")
    void shouldCheckLongUrlExists() {

        repository.save(createMapping());

        assertTrue(repository.existsByLongUrl("https://google.com"));
    }

    @Test
    @DisplayName("Should find by long URL hash")
    void shouldFindByLongUrlHash() {

        UrlMapping saved = repository.save(createMapping());

        Optional<UrlMapping> result =
                repository.findByLongUrlHash(saved.getLongUrlHash());

        assertTrue(result.isPresent());
        assertEquals(
                "abc123",
                result.get().getShortCode());
    }

    @Test
    @DisplayName("Should reject a second mapping with the same long URL")
    void shouldRejectDuplicateLongUrl() {

        repository.saveAndFlush(createMapping());

        UrlMapping duplicate =
                new UrlMapping.Builder()
                        .id(2L)
                        .longUrl("https://google.com")
                        .shortCode("def456")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .build();

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(duplicate));
    }

    @Test
    @DisplayName("Should return empty when short code absent")
    void shouldReturnEmptyWhenShortCodeAbsent() {

        Optional<UrlMapping> result =
                repository.findByShortCode("missing");

        assertTrue(result.isEmpty());
    }
}
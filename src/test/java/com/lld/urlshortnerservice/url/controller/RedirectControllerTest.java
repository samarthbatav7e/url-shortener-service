package com.lld.urlshortnerservice.url.controller;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.excpetions.ResourceNotFoundException;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.service.UrlRedirectService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RedirectController.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlRedirectService redirectService;

    @Test
    @DisplayName("Should redirect to original URL")
    void shouldRedirectSuccessfully() throws Exception {

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(1L)
                        .longUrl("https://www.google.com")
                        .shortCode("abc123")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        when(redirectService.resolve("abc123"))
                .thenReturn(mapping);

        mockMvc.perform(get("/abc123"))

                .andExpect(status().isFound())

                .andExpect(header().string(
                        "Location",
                        "https://www.google.com"));

        verify(redirectService, times(1))
                .resolve("abc123");

        verifyNoMoreInteractions(redirectService);
    }

    @Test
    @DisplayName("Should return redirect with Location header")
    void shouldContainLocationHeader() throws Exception {

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(2L)
                        .longUrl("https://openai.com")
                        .shortCode("xyz789")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        when(redirectService.resolve("xyz789"))
                .thenReturn(mapping);

        mockMvc.perform(get("/xyz789"))

                .andExpect(status().isFound())

                .andExpect(header()
                        .exists("Location"))

                .andExpect(header()
                        .string("Location",
                                "https://openai.com"));
    }

    @Test
    @DisplayName("Should invoke service exactly once")
    void shouldInvokeServiceExactlyOnce() throws Exception {

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(5L)
                        .longUrl("https://github.com")
                        .shortCode("git")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        when(redirectService.resolve(anyString()))
                .thenReturn(mapping);

        mockMvc.perform(get("/git"));

        verify(redirectService, times(1))
                .resolve("git");
    }

    @Test
    @DisplayName("Should pass correct short code to service")
    void shouldPassCorrectShortCode() throws Exception {

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(10L)
                        .longUrl("https://oracle.com")
                        .shortCode("oracle")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        when(redirectService.resolve(anyString()))
                .thenReturn(mapping);

        mockMvc.perform(get("/oracle"));

        verify(redirectService)
                .resolve(eq("oracle"));
    }
    @Disabled
    @Test
    @DisplayName("Should propagate ResourceNotFoundException")
    void shouldThrowResourceNotFoundException() throws Exception {

        when(redirectService.resolve("missing"))
                .thenThrow(
                        new ResourceNotFoundException(
                                "No URL found for short code missing"
                        )
                );

        mockMvc.perform(get("/missing"))

                .andExpect(status().isInternalServerError());

        verify(redirectService)
                .resolve("missing");
    }

    @Test
    @DisplayName("Should return HTTP 302 for valid short code")
    void shouldReturnHttp302() throws Exception {

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(11L)
                        .longUrl("https://spring.io")
                        .shortCode("spring")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        when(redirectService.resolve("spring"))
                .thenReturn(mapping);

        mockMvc.perform(get("/spring"))

                .andExpect(status().isFound());
    }
}
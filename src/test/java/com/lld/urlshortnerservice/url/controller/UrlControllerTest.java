package com.lld.urlshortnerservice.url.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.excpetions.GlobalExceptionHandler;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.dto.CreateUrlResponse;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.mapper.UrlMapper;
import com.lld.urlshortnerservice.url.service.UrlCreationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
@Import(GlobalExceptionHandler.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlCreationService creationService;

    @MockitoBean
    private UrlMapper urlMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create short URL successfully")
    void shouldCreateShortUrl() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://www.google.com",
                        CodeGenerationStrategy.BASE62
                );

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(1L)
                        .longUrl("https://www.google.com")
                        .shortCode("1")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        CreateUrlResponse response =
                new CreateUrlResponse(
                        "1",
                        "http://localhost:8080/1"
                );

        when(creationService.createShortUrl(any(CreateUrlRequest.class)))
                .thenReturn(mapping);

        when(urlMapper.toResponse(mapping))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/urls")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )

                .andExpect(status().isCreated())

                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.shortCode")
                        .value("1"))

                .andExpect(jsonPath("$.shortUrl")
                        .value("http://localhost:8080/1"));

        verify(creationService, times(1))
                .createShortUrl(any(CreateUrlRequest.class));

        verify(urlMapper, times(1))
                .toResponse(mapping);

        verifyNoMoreInteractions(creationService, urlMapper);
    }

    @Test
    @DisplayName("Should pass correct request to service")
    void shouldPassCorrectRequestToService() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://openai.com",
                        CodeGenerationStrategy.SEQUENTIAL
                );

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(5L)
                        .longUrl("https://openai.com")
                        .shortCode("5")
                        .generationStrategy(CodeGenerationStrategy.SEQUENTIAL)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        CreateUrlResponse response =
                new CreateUrlResponse(
                        "5",
                        "http://localhost:8080/5"
                );

        when(creationService.createShortUrl(any()))
                .thenReturn(mapping);

        when(urlMapper.toResponse(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        ArgumentCaptor<CreateUrlRequest> captor =
                ArgumentCaptor.forClass(CreateUrlRequest.class);

        verify(creationService).createShortUrl(captor.capture());

        CreateUrlRequest capturedRequest =
                captor.getValue();

        assertEquals(
                "https://openai.com",
                capturedRequest.getLongUrl());

        assertEquals(
                CodeGenerationStrategy.SEQUENTIAL,
                capturedRequest.getGenerationStrategy());
    }

    @Test
    @DisplayName("Should return correct JSON response")
    void shouldReturnCorrectJsonResponse() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://github.com",
                        CodeGenerationStrategy.BASE62
                );

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(25L)
                        .longUrl("https://github.com")
                        .shortCode("P")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        CreateUrlResponse response =
                new CreateUrlResponse(
                        "P",
                        "http://localhost:8080/P"
                );

        when(creationService.createShortUrl(any()))
                .thenReturn(mapping);

        when(urlMapper.toResponse(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/urls")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.shortCode")
                        .exists())

                .andExpect(jsonPath("$.shortUrl")
                        .exists())

                .andExpect(jsonPath("$.shortCode")
                        .value("P"))

                .andExpect(jsonPath("$.shortUrl")
                        .value("http://localhost:8080/P"));
    }

    @Test
    @DisplayName("Should invoke mapper exactly once")
    void shouldInvokeMapperExactlyOnce() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://oracle.com",
                        CodeGenerationStrategy.BASE62
                );

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(10L)
                        .longUrl("https://oracle.com")
                        .shortCode("A")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(null)
                        .build();

        CreateUrlResponse response =
                new CreateUrlResponse(
                        "A",
                        "http://localhost:8080/A"
                );

        when(creationService.createShortUrl(any()))
                .thenReturn(mapping);

        when(urlMapper.toResponse(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        verify(urlMapper, times(1))
                .toResponse(mapping);
    }

    @Test
    void shouldReturn400WhenLongUrlIsBlank() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest("", CodeGenerationStrategy.BASE62);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath(
                        "$.validationErrors.longUrl")
                        .value("Long URL is required."));

        verifyNoInteractions(creationService);
    }
    @Test
    void shouldReturn400WhenGenerationStrategyIsMissing() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest("https://google.com", null);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath(
                        "$.validationErrors.generationStrategy")
                        .value("Generation Strategy is required."));

        verifyNoInteractions(creationService);
    }
    @Test
    void shouldReturn400WhenRequestBodyIsEmpty() throws Exception {

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath(
                        "$.validationErrors.longUrl")
                        .value("Long URL is required."))
                .andExpect(jsonPath(
                        "$.validationErrors.generationStrategy")
                        .value("Generation Strategy is required."));

        verifyNoInteractions(creationService);
    }
    @Test
    void shouldReturn400WhenLongUrlIsNull() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(null,
                        CodeGenerationStrategy.BASE62);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(creationService);
    }
    @Test
    void shouldReturn400WhenRequestBodyIsMissing() throws Exception {

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(creationService);
    }
    @Test
    @DisplayName("Should pass expiry date to service")
    void shouldPassExpiryDateToService() throws Exception {

        LocalDateTime expiry =
                LocalDateTime.now().plusDays(2);

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://google.com",
                        CodeGenerationStrategy.BASE62);

        request.setExpiresAt(expiry);

        UrlMapping mapping =
                new UrlMapping.Builder()
                        .id(1L)
                        .longUrl(request.getLongUrl())
                        .shortCode("1")
                        .generationStrategy(CodeGenerationStrategy.BASE62)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(expiry)
                        .build();

        CreateUrlResponse response =
                new CreateUrlResponse(
                        "1",
                        "http://localhost:8080/1");

        when(creationService.createShortUrl(any()))
                .thenReturn(mapping);

        when(urlMapper.toResponse(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated());

        ArgumentCaptor<CreateUrlRequest> captor =
                ArgumentCaptor.forClass(CreateUrlRequest.class);

        verify(creationService)
                .createShortUrl(captor.capture());

        assertEquals(
                expiry,
                captor.getValue().getExpiresAt());
    }

    @Test
    @DisplayName("Should return 400 when expiry date is in the past")
    void shouldReturn400WhenExpiryDateIsInPast() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://google.com",
                        CodeGenerationStrategy.BASE62);

        request.setExpiresAt(
                LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.status").value(400))

                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))

                .andExpect(jsonPath("$.validationErrors.expiresAt")
                        .exists());

        verifyNoInteractions(creationService);
    }

}
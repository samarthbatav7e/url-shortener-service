package com.lld.urlshortnerservice.url.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Should create short URL successfully")
    void shouldCreateShortUrl() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://google.com",
                        CodeGenerationStrategy.BASE62);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").exists())
                .andExpect(jsonPath("$.shortUrl").exists());
    }

    @Test
    @DisplayName("Should redirect successfully")
    void shouldRedirectSuccessfully() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://google.com",
                        CodeGenerationStrategy.BASE62);

        String response =
                mockMvc.perform(post("/api/v1/urls")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String shortCode =
                mapper.readTree(response)
                        .get("shortCode")
                        .asText();

        mockMvc.perform(get("/" + shortCode))

                .andExpect(status().isFound())
                .andExpect(header().string(
                        "Location",
                        "https://google.com"));
    }

    @Test
    @DisplayName("Should return same short code for duplicate URL")
    void shouldReturnExistingShortCode() throws Exception {

        CreateUrlRequest request =
                new CreateUrlRequest(
                        "https://duplicate.com",
                        CodeGenerationStrategy.BASE62);

        String first =
                mockMvc.perform(post("/api/v1/urls")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String second =
                mockMvc.perform(post("/api/v1/urls")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals(
                mapper.readTree(first).get("shortCode").asText(),
                mapper.readTree(second).get("shortCode").asText()
        );
    }
}
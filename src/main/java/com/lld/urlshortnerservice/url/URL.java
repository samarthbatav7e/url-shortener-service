package com.lld.urlshortnerservice.url;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;

import java.time.LocalDateTime;

public class URL {
    private long id;
    private String longUrl;
    private String shortCode;
    private CodeGenerationStrategy generationStrategy;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;


}

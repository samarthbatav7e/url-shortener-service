package com.lld.urlshortnerservice.url.dto;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;

public class CreateUrlRequest {
    String longUrl;
    private CodeGenerationStrategy generationStrategy;

}

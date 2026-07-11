package com.lld.urlshortnerservice.url.strategy.generator;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;

public interface ShortCodeGenerator {
    String generate();
    CodeGenerationStrategy getStrategy();

}

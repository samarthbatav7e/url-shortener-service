package com.lld.urlshortnerservice.url.strategy.impl;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;

public class SequentialGenerator implements ShortCodeGenerator {

    @Override
    public String generate()
    {
        return null;
    }
    @Override
    public CodeGenerationStrategy getStrategy()
    {
        return CodeGenerationStrategy.SEQUENTIAL;
    }
}

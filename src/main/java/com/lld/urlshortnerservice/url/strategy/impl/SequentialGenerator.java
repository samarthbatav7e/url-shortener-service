package com.lld.urlshortnerservice.url.strategy.impl;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class SequentialGenerator implements ShortCodeGenerator {

    @Override
    public String generate(long id)
    {
        return String.valueOf(id);
    }
    @Override
    public CodeGenerationStrategy getStrategy()
    {
        return CodeGenerationStrategy.SEQUENTIAL;
    }
}

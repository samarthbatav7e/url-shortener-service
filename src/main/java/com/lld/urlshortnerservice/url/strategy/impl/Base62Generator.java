package com.lld.urlshortnerservice.url.strategy.impl;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;

public class Base62Generator implements ShortCodeGenerator {
    @Override
    public String generate()
    {
        return null;
    }
    @Override
    public CodeGenerationStrategy getStrategy()
    {
        return CodeGenerationStrategy.BASE62;
    }
}

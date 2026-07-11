package com.lld.urlshortnerservice.url.strategy.factory;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ShortCodeGeneratorFactory {
    private final Map<CodeGenerationStrategy, ShortCodeGenerator> generators=
            new EnumMap<>(CodeGenerationStrategy.class);

    public ShortCodeGeneratorFactory(List<ShortCodeGenerator> generatorList)
    {
        for(ShortCodeGenerator generator:generatorList)
        {
            generators.put(generator.getStrategy(),generator);
        }
    }
    public ShortCodeGenerator getGenerator(CodeGenerationStrategy strategy)
    {
        ShortCodeGenerator shortCodeGenerator=generators.get(strategy);

        if(shortCodeGenerator==null)
        {
            throw new IllegalArgumentException("No generator found for strategy " + strategy);
        }
        return shortCodeGenerator;
    }

}

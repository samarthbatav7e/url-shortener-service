package com.lld.urlshortnerservice.url.strategy.factory;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ShortCodeGeneratorFactory {
    private  static final Logger LOGGER= LoggerFactory.getLogger(ShortCodeGeneratorFactory.class);

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
        LOGGER.debug("Using {} generator", strategy);
        return shortCodeGenerator;
    }

}

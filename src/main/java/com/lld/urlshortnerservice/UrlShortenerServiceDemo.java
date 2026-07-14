package com.lld.urlshortnerservice;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.idgenerator.AtomicIdGenerator;
import com.lld.urlshortnerservice.common.idgenerator.IdGenerator;
import com.lld.urlshortnerservice.url.dto.CreateUrlRequest;
import com.lld.urlshortnerservice.url.dto.CreateUrlResponse;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import com.lld.urlshortnerservice.url.repository.InMemoryUrlMappingRepository;
import com.lld.urlshortnerservice.url.repository.UrlMappingRepository;
import com.lld.urlshortnerservice.url.service.UrlCreationService;
import com.lld.urlshortnerservice.url.strategy.factory.ShortCodeGeneratorFactory;
import com.lld.urlshortnerservice.url.strategy.impl.Base62Generator;
import com.lld.urlshortnerservice.url.strategy.impl.SequentialGenerator;

import java.util.List;

public class UrlShortenerServiceDemo {
    private static final String BASE_URL="http://localhost:8080/";

    public static void main(String[] args)
    {
        UrlMappingRepository repository=new InMemoryUrlMappingRepository();
        IdGenerator idGenerator=new AtomicIdGenerator();
        ShortCodeGeneratorFactory factory=new ShortCodeGeneratorFactory(
                List.of(new Base62Generator(),
                        new SequentialGenerator())
        );

        UrlCreationService service=new UrlCreationService(
                repository,
                idGenerator,
                factory
        );

        CreateUrlRequest request=new CreateUrlRequest(
                "https://www.google.com/search?q=spotify",
                CodeGenerationStrategy.BASE62
        );

        UrlMapping mapping=service.createShortUrl(request);

        CreateUrlResponse response=new CreateUrlResponse(
                mapping.getShortCode(),
                BASE_URL+mapping.getShortCode()
        );

        System.out.println("========= URL CREATED ==========");
        System.out.println("\n");
        System.out.println("Long URL " + mapping.getLongUrl() );
        System.out.println("\n");
        System.out.println("Short code " + response.getShortCode());
        System.out.println("\n");
        System.out.println("Short URL " + response.getShortUrl());
    }
}

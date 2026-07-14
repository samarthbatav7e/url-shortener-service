package com.lld.urlshortnerservice.url.repository;

import com.lld.urlshortnerservice.url.entity.UrlMapping;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUrlMappingRepository implements UrlMappingRepository{
    private final Map<String, UrlMapping> shortCodeStore;
    private final Map<String, UrlMapping> longUrlStore;

    public InMemoryUrlMappingRepository()
    {
        this.shortCodeStore=new HashMap<>();
        this.longUrlStore=new HashMap<>();
    }

    @Override
    public UrlMapping save(UrlMapping urlMapping)
    {
        shortCodeStore.put(urlMapping.getShortCode(), urlMapping);
        longUrlStore.put(urlMapping.getLongUrl(), urlMapping);

        return urlMapping;
    }
    @Override
    public Optional<UrlMapping> findByLongUrl(String longUrl)
    {
        return Optional.ofNullable(longUrlStore.get(longUrl));
    }
    @Override
    public Optional<UrlMapping> findByShortCode(String shortCode)
    {
        return Optional.ofNullable(shortCodeStore.get(shortCode));
    }
    @Override
    public boolean existsByShortCode(String shortCode)
    {
        return shortCodeStore.containsKey(shortCode);
    }

}

package com.lld.urlshortnerservice.cache.service;

import com.lld.urlshortnerservice.cache.repository.UrlCacheRepository;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlCacheService {

    private final UrlCacheRepository cacheRepository;

    public UrlCacheService(UrlCacheRepository cacheRepository)
    {
        this.cacheRepository=cacheRepository;
    }

    public Optional<UrlMapping> getCachedUrlMapping(String shortCode)
    {
        return cacheRepository.get(shortCode);
    }
    public void cacheUrlMapping(UrlMapping urlMapping)
    {
        cacheRepository.save(urlMapping);
    }
    public void evict(String shortCode)
    {
        cacheRepository.delete(shortCode);
    }



}

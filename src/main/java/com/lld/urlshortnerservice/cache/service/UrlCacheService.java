package com.lld.urlshortnerservice.cache.service;

import com.lld.urlshortnerservice.cache.repository.UrlCacheRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlCacheService {

    private final UrlCacheRepository cacheRepository;

    public UrlCacheService(UrlCacheRepository cacheRepository)
    {
        this.cacheRepository=cacheRepository;
    }

    public Optional<String> getCachedUrl(String shortCode)
    {
        return cacheRepository.get(shortCode);
    }
    public void cacheUrl(String shortCode, String longUrl)
    {
        cacheRepository.save(shortCode,longUrl);
    }
    public void evict(String shortCode)
    {
        cacheRepository.delete(shortCode);
    }



}

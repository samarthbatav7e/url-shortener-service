package com.lld.urlshortnerservice.cache.impl;

import com.lld.urlshortnerservice.cache.service.CacheService;

public class RedisCacheService implements CacheService {
    @Override
    public String getLongUrl(String shortCOde)
    {
        return null;
    }
    @Override
    public String getShortCode(String longUrl)
    {
        return null;
    }
    @Override
    public void put(String shortCode, String longUrl)
    {

    }
    @Override
    public void evict(String shortCode)
    {

    }

}

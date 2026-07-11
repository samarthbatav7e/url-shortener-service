package com.lld.urlshortnerservice.cache.service;

public interface CacheService {

    String getLongUrl(String ShortCode);
    String getShortCode(String longUrl);

   void put(String shortCode,String longUrl);
   void evict(String shortCode);

}

package com.lld.urlshortnerservice.cache.repository;

import java.util.Optional;

public interface UrlCacheRepository {
    Optional<String> get(String shortCode);
    void save(String shortCode, String longUrl);
    void delete(String shortCode);

}

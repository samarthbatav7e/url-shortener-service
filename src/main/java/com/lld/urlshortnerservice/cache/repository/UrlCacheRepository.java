package com.lld.urlshortnerservice.cache.repository;

import com.lld.urlshortnerservice.url.entity.UrlMapping;

import java.util.Optional;

public interface UrlCacheRepository {
    Optional<UrlMapping> get(String shortCode);
    void save(UrlMapping urlMapping);
    void delete(String shortCode);

}

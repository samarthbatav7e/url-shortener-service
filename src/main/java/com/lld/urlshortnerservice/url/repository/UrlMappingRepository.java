package com.lld.urlshortnerservice.url.repository;

import com.lld.urlshortnerservice.url.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlMappingRepository {

    UrlMapping save(UrlMapping urlMapping);
    void delete(UrlMapping urlMapping);
    Optional<UrlMapping>findByLongUrl(String longUrl);
    Optional<UrlMapping>findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    boolean existsByLongUrl(String longUrl);

}

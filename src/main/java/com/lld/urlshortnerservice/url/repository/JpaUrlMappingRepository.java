package com.lld.urlshortnerservice.url.repository;

import com.lld.urlshortnerservice.url.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUrlMappingRepository extends JpaRepository<UrlMapping,Long> {

    Optional<UrlMapping>findByLongUrl(String longUrl);
    Optional<UrlMapping>findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    boolean existsByLongUrl(String longUrl);


}

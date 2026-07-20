package com.lld.urlshortnerservice.url.repository;

import com.lld.urlshortnerservice.url.entity.UrlMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUrlMappingRepositoryAdapter implements  UrlMappingRepository{

    private static final Logger LOGGER= LoggerFactory.getLogger(JpaUrlMappingRepositoryAdapter.class);

    private final JpaUrlMappingRepository jpaRepository;

    public JpaUrlMappingRepositoryAdapter(JpaUrlMappingRepository repository)
    {
        this.jpaRepository=repository;
    }
    @Override
    public UrlMapping save(UrlMapping urlMapping)
    {
        LOGGER.debug("Saving URL Mapping {}", urlMapping.getShortCode());
        return jpaRepository.save(urlMapping);
    }
    @Override
    public void delete(UrlMapping urlMapping)
    {
        LOGGER.debug("Deleting expired URL Mapping {}", urlMapping.getShortCode());
        jpaRepository.delete(urlMapping);
    }
    @Override
    public Optional<UrlMapping>findByLongUrl(String longUrl)
    {
        return jpaRepository.findByLongUrl(longUrl);
    }
    @Override
    public Optional<UrlMapping>findByShortCode(String shortCode)
    {
        LOGGER.debug("Finding short code {}", shortCode);
        return jpaRepository.findByShortCode(shortCode);
    }
    @Override
    public boolean existsByShortCode(String shortCode)
    {
        return jpaRepository.existsByShortCode(shortCode);
    }
    @Override
    public boolean existsByLongUrl(String longUrl)
    {
        return jpaRepository.existsByLongUrl(longUrl);
    }
}

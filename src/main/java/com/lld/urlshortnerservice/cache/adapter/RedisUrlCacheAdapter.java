package com.lld.urlshortnerservice.cache.adapter;

import com.lld.urlshortnerservice.cache.repository.UrlCacheRepository;
import com.lld.urlshortnerservice.url.entity.UrlMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class RedisUrlCacheAdapter implements UrlCacheRepository {

    private final RedisTemplate<String, UrlMapping>redisTemplate;
    private final Duration ttl;

    public RedisUrlCacheAdapter(RedisTemplate<String,UrlMapping> redisTemplate, @Value("${url.cache.ttl}") long ttlSeconds)
    {
        this.redisTemplate=redisTemplate;
        this.ttl=Duration.ofSeconds(ttlSeconds);
    }

    private  Duration calculateTTl(UrlMapping urlMapping)
    {
        if(urlMapping.getExpiresAt()!=null)
        {
            Duration TTL=Duration.between(LocalDateTime.now(), urlMapping.getExpiresAt());

            if(!TTL.isNegative() && !TTL.isZero())
            {
                return TTL;
            }
            return Duration.ZERO;
        }
        return ttl;

    }

    @Override
    public Optional<UrlMapping> get(String shortCode)
    {
        return Optional.ofNullable(redisTemplate.opsForValue().get(shortCode));
    }

    @Override
    public void save(UrlMapping urlMapping)
    {
        Duration TTL=calculateTTl(urlMapping);

        redisTemplate.opsForValue().set(urlMapping.getShortCode(),
                urlMapping,
                TTL
        );
    }

    @Override
    public void delete(String shortCode)
    {
     redisTemplate.delete(shortCode);
    }


}

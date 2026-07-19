package com.lld.urlshortnerservice.cache.adapter;

import com.lld.urlshortnerservice.cache.repository.UrlCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RedisUrlCacheAdapter implements UrlCacheRepository {

    private final RedisTemplate<String, String>redisTemplate;
    private final Duration ttl;

    public RedisUrlCacheAdapter(RedisTemplate<String,String> redisTemplate, @Value("${url.cache.ttl}") long ttlSeconds)
    {
        this.redisTemplate=redisTemplate;
        this.ttl=Duration.ofSeconds(ttlSeconds);
    }
    @Override
    public Optional<String> get(String shortCode)
    {
        return Optional.ofNullable(redisTemplate.opsForValue().get(shortCode));
    }

    @Override
    public void save(String shortCode, String longUrl)
    {
        redisTemplate.opsForValue().set(shortCode, longUrl,ttl);
    }

    @Override
    public void delete(String shortCode)
    {
     redisTemplate.delete(shortCode);
    }


}

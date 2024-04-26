package com.hit.joonggonara.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, String value, Integer expirationTime){
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(expirationTime));
    }
    public Optional<String> get(String key){
        Object value = redisTemplate.opsForValue().get(key);
        return value==null? Optional.empty() : Optional.of(String.valueOf(value));
    }
}

package com.hit.joonggonara.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.hit.joonggonara.common.properties.RedisProperties.BLACK_LIST_VALUE;


@RequiredArgsConstructor
@Repository
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;


    public Optional<String> get(String key){
        Object value = redisTemplate.opsForValue().get(key);
        return value==null? Optional.empty() : Optional.of(String.valueOf(value));
    }

    public void removeAndSave(String key, String value, Integer expirationTime) {
       get(key).ifPresent( v -> {redisTemplate.delete(key);});
       redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(expirationTime));
    }

    public void save(String key, Object value, Integer expirationTime) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(expirationTime));
    }

    public void addBlackList(String token){
        redisTemplate.opsForValue()
                .set(token, BLACK_LIST_VALUE, jwtUtil.getExpired(token), TimeUnit.MILLISECONDS);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

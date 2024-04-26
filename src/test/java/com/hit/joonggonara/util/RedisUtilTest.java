package com.hit.joonggonara.util;

import com.hit.joonggonara.common.config.RedisConfig;
import com.hit.joonggonara.common.properties.RedisProperties;
import com.hit.joonggonara.common.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(RedisConfig.class)
@SpringBootTest
class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;


    @MethodSource
    @ParameterizedTest
    @DisplayName("[Redis] 레디스 저장 및 조회 테스트 - 10전 조회, 10후 조회")
    void saveRedisTest(String key, String value, Integer expirationTime) throws Exception
    {
        //given
        redisUtil.save(key, value, expirationTime);
        //when
        String expectedValue = redisUtil.get(key).get();
        //then
        assertThat(expectedValue).isEqualTo(value);
        Thread.sleep(5000);
        Optional<String> expectedNullValue = redisUtil.get(key);
        assertThat(expectedNullValue).isEmpty();
    }
    static Stream<Arguments> saveRedisTest(){
        String email = "test@email.com";
        return Stream.of(
                Arguments.of(RedisProperties.EMAIL_KEY + email, "123456", 5),
                Arguments.of(RedisProperties.PHONE_NUMBER_KEY + email, "123456", 5),
                Arguments.of(RedisProperties.REFRESH_TOKEN_KEY + email, "refreshToken", 5)
        );
    }


}
package com.hit.joonggonara.common.util;

import com.hit.joonggonara.common.config.RedisConfig;
import com.hit.joonggonara.common.properties.RedisProperties;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.dto.login.TokenDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @Autowired
    private JwtUtil jwtUtil;


    @MethodSource
    @ParameterizedTest
    @DisplayName("[Redis][Get] 조회 테스트 - 10전 조회, 10후 조회")
    void saveRedisTest(String key, String value, Integer expirationTime) throws Exception
    {
        //given
        redisUtil.removeAndSave(key, value, expirationTime);
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

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Redis][Save] 값이 존재 할 경우 삭제 후 저장")
    void redisDeleteAndSaveTest(String key, String value, Integer expirationTime) throws Exception
    {
        //given
        String testValue = "test redis save";
        redisUtil.removeAndSave(key,value, expirationTime);
        //when
        redisUtil.removeAndSave(key,value+testValue, expirationTime);
        String expectedValue = redisUtil.get(key).get();
        //then
        assertThat(expectedValue).isNotEqualTo(value);
        assertThat(expectedValue).isEqualTo(value+testValue);


    }
    static Stream<Arguments> redisDeleteAndSaveTest(){
        String email = "test@email.com";
        return Stream.of(
                Arguments.of(RedisProperties.EMAIL_KEY + email, "123456", 5),
                Arguments.of(RedisProperties.PHONE_NUMBER_KEY + email, "123456", 5),
                Arguments.of(RedisProperties.REFRESH_TOKEN_KEY + email, "refreshToken", 5)
        );
    }
    
    @Test
    @DisplayName("[Redis][BlackList] 블랙리스트에 토큰을 추가하고 기존 토큰에 남은 시간으로 블랙리스트 TTL 을 설정")
    void addBlackListTest() throws Exception
    {
        //given
        TokenDto tokenDto = jwtUtil.createToken("testId", Role.USER, LoginType.GENERAL);
        //when
        redisUtil.addBlackList(tokenDto.accessToken());
        String expectedValue = redisUtil.get(tokenDto.accessToken()).get();
        //then
        assertThat(RedisProperties.BLACK_LIST_VALUE).isEqualTo(expectedValue);

    }


}
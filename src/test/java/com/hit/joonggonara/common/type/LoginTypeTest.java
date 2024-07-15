package com.hit.joonggonara.common.type;

import org.assertj.core.util.Streams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LoginTypeTest {


    @MethodSource
    @ParameterizedTest
    @DisplayName("[LoginType] 로그인 타입 체크 테스트")
    void checkLoginTypeTest(String expectedValue, LoginType expectedType) throws Exception
    {
        //given

        //when
        LoginType loginType = LoginType.checkType(expectedValue);
        //then
        assertThat(loginType).isEqualTo(expectedType);
    }

    static Stream<Arguments> checkLoginTypeTest(){
        return Stream.of(
                Arguments.of("google", LoginType.GOGGLE),
                Arguments.of("Google", LoginType.GOGGLE),
                Arguments.of("GOOGLE", LoginType.GOGGLE),
                Arguments.of("naver", LoginType.NAVER),
                Arguments.of("Naver", LoginType.NAVER),
                Arguments.of("NAVER", LoginType.NAVER),
                Arguments.of("kakao", LoginType.KAKAO),
                Arguments.of("Kakao", LoginType.KAKAO),
                Arguments.of("KAKAO", LoginType.KAKAO)
        );
    }

}
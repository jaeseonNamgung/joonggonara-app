package com.hit.joonggonara.dto.request;

import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @MethodSource
    @ParameterizedTest(name = "{index} - {0}, {1}")
    @DisplayName("[API][유효성] 로그인 유효성 검증 테스트")
    void loginValidationTest(String email, String password, String message) throws Exception
    {
        //given
        LoginRequest loginRequest = LoginRequest.of(email, password);
        //when
        Set<ConstraintViolation<LoginRequest>> validate = validator.validate(loginRequest, ValidationSequence.class);
        //then
        assertThat(validate).isNotEmpty();
        validate.forEach(v ->{
            assertThat(v.getMessage()).isEqualTo(message);
        });
    }

    static Stream<Arguments> loginValidationTest(){
        return Stream.of(
                Arguments.of("email", "abc1234*", "이메일 주소를 정확히 입력해주세요."),
                Arguments.of(" ", "abc1234*", "이메일 또는 비밀번호를 입력해주세요." ),
                Arguments.of("email@naver.com", " ", "이메일 또는 비밀번호를 입력해주세요." ),
                Arguments.of(null, "abc1234*", "이메일 또는 비밀번호를 입력해주세요." ),
                Arguments.of("email@naver.com", null, "이메일 또는 비밀번호를 입력해주세요." ),
                Arguments.of("", "", "이메일 또는 비밀번호를 입력해주세요." ),
                Arguments.of(null, null, "이메일 또는 비밀번호를 입력해주세요." )
        );
    }

}
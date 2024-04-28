package com.hit.joonggonara.dto.login.request;

import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.dto.request.login.SignUpRequest;
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

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;
import static org.assertj.core.api.Assertions.assertThat;

class SignUpRequestTest {

    private Validator sut;

    @BeforeEach
    void setUp(){
        ValidatorFactory  validatorFactory = Validation.buildDefaultValidatorFactory();
        sut = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[API][Validation] SignUpRequest Validation 검증 테스트")
    void signUpValidationTest(
            String userId,
            String email,
            String password,
            String name,
            String nickName,
            String phoneNumber,
            String message) throws Exception {
        //given
        SignUpRequest signUpRequest = SignUpRequest.of(userId, email, password, name, nickName, phoneNumber);
        //when

        Set<ConstraintViolation<SignUpRequest>> expectedValidate = sut.validate(signUpRequest, ValidationSequence.class);
        assertThat(expectedValidate).isNotEmpty();
        //then
        expectedValidate.forEach(v -> {
            assertThat(v.getMessage()).isEqualTo(message);
        });
    }

    static Stream<Arguments> signUpValidationTest() {
        return Stream.of(
                Arguments.of(null, "email@nave.com", "Abc1234567*", "name", "nickName", "010-0000-0000", USER_ID_NOT_BLANK),
                Arguments.of("testId", null, "Abc1234567*", "name", "nickName", "010-0000-0000", EMAIL_NOT_BLANK),
                Arguments.of("testId","email", "Abc1234567*", "name", "nickName", "010-0000-0000", EMAIL),
                Arguments.of("testId","email@nave.com", null, "name", "nickName", "010-0000-0000", PASSWORD_NOT_BLANK),
                Arguments.of("testId","email@nave.com", "abc", "name", "nickName", "010-0000-0000", PASSWORD_PATTERN),
                Arguments.of("testId","email@nave.com", "Abc1234567*", null, "nickName", "010-0000-0000",NAME_NOT_BLANK),
                Arguments.of("testId","email@nave.com", "Abc1234567*", "name", null, "010-0000-0000", NICK_NAME_NOT_BLANK),
                Arguments.of("testId","email@nave.com", "Abc1234567*", "name", "nickName", null, PHONE_NUMBER_NOT_BLANK)
        );
    }


}
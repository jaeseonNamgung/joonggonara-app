package com.hit.joonggonara.dto.request;

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

import static org.assertj.core.api.Assertions.assertThat;

class SignUpRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp(){
        ValidatorFactory  validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[API][Validation] SignUpRequest Validation 검증 테스트")
    void signUpValidationTest(
            String email,
            String password,
            String name,
            String nickName,
            String school,
            String phoneNumber,
            String message) throws Exception {
        //given
        SignUpRequest signUpRequest = SignUpRequest.of(email, password, name, nickName, school, phoneNumber);
        //when

        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(signUpRequest, ValidationSequence.class);
        assertThat(validate).isNotEmpty();
        //then
        validate.forEach(v -> {
            assertThat(v.getMessage()).isEqualTo(message);
        });
    }

    static Stream<Arguments> signUpValidationTest() {
        return Stream.of(
                Arguments.of(null, "Abc1234567*", "name", "nickName", "school", "010-0000-0000", "이메일을 입력해주세요"),
                Arguments.of("email", "Abc1234567*", "name", "nickName", "school", "010-0000-0000", "이메일 주소를 정확히 입력해주세요."),
                Arguments.of("email@nave.com", null, "name", "nickName", "school", "010-0000-0000", "비밀번호를 입력해주세요"),
                Arguments.of("email@nave.com", "abc", "name", "nickName", "school", "010-0000-0000", "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용해 주세요."),
                Arguments.of("email@nave.com", "Abc1234567*", "name", "nickName", "school", null, "전화번호를 입력해주세요")
        );
    }


}
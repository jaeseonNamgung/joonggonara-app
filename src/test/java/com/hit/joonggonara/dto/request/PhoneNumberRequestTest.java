package com.hit.joonggonara.dto.request;

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

class PhoneNumberRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Request][Validation] request 유효성 검증 테스트")
    void validationTest(String expectedPhoneNumber, String expectedMessage) throws Exception
    {
        Set<ConstraintViolation<PhoneNumberRequest>> validate =
                validator.validate(
                        PhoneNumberRequest.of(expectedPhoneNumber)
                );
        assertThat(validate).isNotEmpty();
        validate.forEach(value->{
            assertThat(value.getMessage()).isEqualTo(expectedMessage);
        });
    }

    static Stream<Arguments> validationTest(){
        return Stream.of(
                Arguments.of("", "전화번호를 입력해주세요."),
                Arguments.of(null, "전화번호를 입력해주세요."),
                Arguments.of(" ", "전화번호를 입력해주세요.")
        );
    }
}
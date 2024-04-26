package com.hit.joonggonara.dto.request;

import com.hit.joonggonara.custom.validation.ValidationSequence;
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


class EmailRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Request][Validation] request 유효성 검증 오류")
    void validationTest(String expectedEmail, String expectedMessage) throws Exception
    {
        Set<ConstraintViolation<EmailRequest>> validate =
                validator.validate(EmailRequest.of(expectedEmail), ValidationSequence.class);

        assertThat(validate).isNotEmpty();

        validate.forEach(value->{
            assertThat(value.getMessage()).isEqualTo(expectedMessage);
        });
    }
    static Stream<Arguments> validationTest(){
        return Stream.of(
                Arguments.of("", "이메일을 입력해주세요."),
                Arguments.of(null, "이메일을 입력해주세요."),
                Arguments.of(" ", "이메일을 입력해주세요."),
                Arguments.of("test", "이메일 주소를 정확히 입력해주세요.")
        );
    }



}
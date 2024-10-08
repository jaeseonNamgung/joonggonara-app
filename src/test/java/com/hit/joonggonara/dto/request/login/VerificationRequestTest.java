package com.hit.joonggonara.dto.request.login;

import com.hit.joonggonara.dto.request.login.VerificationRequest;
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

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.VERIFICATION_CODE_NOT_BLANK;
import static org.assertj.core.api.Assertions.assertThat;

class VerificationRequestTest {

    private Validator sut;

    @BeforeEach
    void setUp(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        sut = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Request][Validation] request 유효성 검증 테스트")
    void validationTest(String expectedVerificationCode, String expectedMessage) throws Exception
    {
        Set<ConstraintViolation<VerificationRequest>> expectedValidate =
                sut.validate(
                        VerificationRequest.of("+8617212345678", expectedVerificationCode)
                );
        assertThat(expectedValidate).isNotEmpty();
        expectedValidate.forEach(value->{
            assertThat(value.getMessage()).isEqualTo(expectedMessage);
        });
    }

    static Stream<Arguments> validationTest(){
        return Stream.of(
                Arguments.of("", VERIFICATION_CODE_NOT_BLANK),
                Arguments.of(null, VERIFICATION_CODE_NOT_BLANK),
                Arguments.of(" ", VERIFICATION_CODE_NOT_BLANK)
        );
    }

}
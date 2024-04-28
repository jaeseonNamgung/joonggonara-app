package com.hit.joonggonara.dto.login.request;

import com.hit.joonggonara.dto.request.login.FindUserIdBySmsRequest;
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

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.NAME_NOT_BLANK;
import static com.hit.joonggonara.common.properties.ValidationMessageProperties.PHONE_NUMBER_NOT_BLANK;
import static org.assertj.core.api.Assertions.assertThat;

class FindUserIdBySmsRequestTest {
    private Validator sut;

    @BeforeEach
    void setUp(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        sut = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Request][Validation] request 유효성 검증 테스트")
    void validationTest(String userId, String phoneNumber, String message) throws Exception
    {
        Set<ConstraintViolation<FindUserIdBySmsRequest>> expectedValidate =
                sut.validate(
                        FindUserIdBySmsRequest.of(userId, phoneNumber)
                );
        assertThat(expectedValidate).isNotEmpty();
        expectedValidate.forEach(value->{
            assertThat(value.getMessage()).isEqualTo(message);
        });
    }

    static Stream<Arguments> validationTest(){
        return Stream.of(
                Arguments.of("", "+8612345678", NAME_NOT_BLANK),
                Arguments.of(null, "+8612345678", NAME_NOT_BLANK),
                Arguments.of(" ", "+8612345678", NAME_NOT_BLANK),
                Arguments.of("hong","", PHONE_NUMBER_NOT_BLANK),
                Arguments.of("hong",null, PHONE_NUMBER_NOT_BLANK),
                Arguments.of("hong", " ", PHONE_NUMBER_NOT_BLANK)

        );
    }
}
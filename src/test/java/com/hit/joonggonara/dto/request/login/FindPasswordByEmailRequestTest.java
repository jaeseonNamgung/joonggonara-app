package com.hit.joonggonara.dto.request.login;

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

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;
import static org.assertj.core.api.Assertions.assertThat;

class FindPasswordByEmailRequestTest {

    private Validator sut;

    @BeforeEach
    void setUp(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        sut = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Validation] 유효성 검증 오류 시 각 오류 메시지 리턴")
    void validationTest(
            String expectedName,
            String expectedUserId,
            String expectedEmail,
            String expectedMessage) throws Exception
    {
        //given
        FindPasswordByEmailRequest findUserIdByEmailRequest = FindPasswordByEmailRequest.of(expectedName, expectedUserId, expectedEmail);
        //when
        Set<ConstraintViolation<FindPasswordByEmailRequest>> expectedValidate =
                sut.validate(findUserIdByEmailRequest, ValidationSequence.class);
        //then
        assertThat(expectedValidate).isNotEmpty();

        expectedValidate.forEach(validate->{
            assertThat(validate.getMessage()).isEqualTo(expectedMessage);
        });
    }

    static Stream<Arguments> validationTest(){
        return Stream.of(
                Arguments.of(" ","testId","test@email.com", NAME_NOT_BLANK),
                Arguments.of(null,"testId","test@email.com", NAME_NOT_BLANK),
                Arguments.of("","testId","test@email.com", NAME_NOT_BLANK),
                Arguments.of("userId"," ","test@email.com", USER_ID_NOT_BLANK),
                Arguments.of("userId",null,"test@email.com", USER_ID_NOT_BLANK),
                Arguments.of("userId","","test@email.com", USER_ID_NOT_BLANK),
                Arguments.of("userId","testId"," ", EMAIL_NOT_BLANK),
                Arguments.of("userId","testId",null, EMAIL_NOT_BLANK),
                Arguments.of("userId","testId","", EMAIL_NOT_BLANK),
                Arguments.of("userId","testId","test", EMAIL)
        );
    }


}
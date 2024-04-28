package com.hit.joonggonara.dto.login.request;

import com.hit.joonggonara.dto.request.login.FindPasswordBySmsRequest;
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

class FindPasswordBySmsRequestTest {

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
            String expectedPhoneNumber,
            String expectedMessage) throws Exception
    {
        //given
        FindPasswordBySmsRequest findUserIdByEmailRequest = FindPasswordBySmsRequest.of(expectedName, expectedUserId, expectedPhoneNumber);
        //when
        Set<ConstraintViolation<FindPasswordBySmsRequest>> expectedValidate =
                sut.validate(findUserIdByEmailRequest);
        //then
        assertThat(expectedValidate).isNotEmpty();

        expectedValidate.forEach(validate->{
            assertThat(validate.getMessage()).isEqualTo(expectedMessage);
        });
    }

    static Stream<Arguments> validationTest(){
        return Stream.of(
                Arguments.of(" ","testId","+8617512345678", NAME_NOT_BLANK),
                Arguments.of(null,"testId","+8617512345678", NAME_NOT_BLANK),
                Arguments.of("","testId","+8617512345678", NAME_NOT_BLANK),
                Arguments.of("userId"," ","+8617512345678", USER_ID_NOT_BLANK),
                Arguments.of("userId",null,"+8617512345678", USER_ID_NOT_BLANK),
                Arguments.of("userId","","+8617512345678", USER_ID_NOT_BLANK),
                Arguments.of("userId","testId"," ", PHONE_NUMBER_NOT_BLANK),
                Arguments.of("userId","testId",null, PHONE_NUMBER_NOT_BLANK),
                Arguments.of("userId","testId","", PHONE_NUMBER_NOT_BLANK)
        );
    }

}
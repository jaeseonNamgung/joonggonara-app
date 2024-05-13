package com.hit.joonggonara.dto.request.login;

import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.dto.request.login.FindUserIdByEmailRequest;
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

class FindUserIdByEmailRequestTest {

    private Validator sut;

    @BeforeEach
    void setUp(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        sut = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Validation] 유효성 검증 오류 시 각 오류 메시지 리턴")
    void validationTest(String name, String email, String message) throws Exception
    {
        //given
        FindUserIdByEmailRequest findUserIdByEmailRequest = FindUserIdByEmailRequest.of(name, email);
        //when
        Set<ConstraintViolation<FindUserIdByEmailRequest>> expectedValidate =
                sut.validate(findUserIdByEmailRequest, ValidationSequence.class);
        //then
        assertThat(expectedValidate).isNotEmpty();

        expectedValidate.forEach(validate->{
            assertThat(validate.getMessage()).isEqualTo(message);
        });
    }

    static Stream<Arguments> validationTest(){
        return Stream.of(
                Arguments.of(" ","test@principal.com", NAME_NOT_BLANK),
                Arguments.of(null,"test@principal.com", NAME_NOT_BLANK),
                Arguments.of("","test@principal.com", NAME_NOT_BLANK),
                Arguments.of("principal"," ", EMAIL_NOT_BLANK),
                Arguments.of("principal",null, EMAIL_NOT_BLANK),
                Arguments.of("principal","", EMAIL_NOT_BLANK),
                Arguments.of("principal","test", EMAIL)
        );
    }


}
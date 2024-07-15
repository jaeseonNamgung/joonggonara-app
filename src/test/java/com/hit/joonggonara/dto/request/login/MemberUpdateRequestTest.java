package com.hit.joonggonara.dto.request.login;


import com.hit.joonggonara.common.custom.validation.ValidationSequence;
import com.hit.joonggonara.common.properties.ValidationMessageProperties;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.*;
import static org.assertj.core.api.Assertions.assertThat;


class MemberUpdateRequestTest {

    private Validator sut;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        sut = validatorFactory.getValidator();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Validation] Request 검증 테스트")
    void ValidationTest(String nickName, String email, String phoneNumber, String message) throws Exception
    {
        //given
        MemberUpdateRequest memberUpdateRequest =
                MemberUpdateRequest.of(nickName, email, phoneNumber, "profile", true);
        //when
        Set<ConstraintViolation<MemberUpdateRequest>> expectedValidate = sut.validate(memberUpdateRequest, ValidationSequence.class);
        assertThat(expectedValidate).isNotEmpty();
        //then
        expectedValidate.forEach(v -> {
            assertThat(v.getMessage()).isEqualTo(message);
        });

    }
    static Stream<Arguments> ValidationTest(){
        return Stream.of(
                Arguments.of(null,"test@email.com", "+8617545562261", NICK_NAME_NOT_BLANK),
                Arguments.of(" ","test@email.com", "+8617545562261", NICK_NAME_NOT_BLANK),
                Arguments.of("nickName",null, "+8617545562261", EMAIL_NOT_BLANK),
                Arguments.of("nickName"," ", "+8617545562261", EMAIL_NOT_BLANK),
                Arguments.of("nickName","test", "+8617545562261", EMAIL),
                Arguments.of("nickName","test@email.com", null, PHONE_NUMBER_NOT_BLANK),
                Arguments.of("nickName","test@email.com", " ", PHONE_NUMBER_NOT_BLANK)
        );
    }

}
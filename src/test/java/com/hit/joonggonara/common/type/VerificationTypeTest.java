package com.hit.joonggonara.common.type;

import com.hit.joonggonara.common.error.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class VerificationTypeTest {


    @MethodSource
    @ParameterizedTest
    @DisplayName("[Enum] 문자열 -> VerificationType으로 변환")
    void toEnumTest(String sut, VerificationType verificationType) throws Exception
    {
        VerificationType expectedType = VerificationType.toEnum(sut);

        assertThat(verificationType).isEqualTo(expectedType);
    }
    static Stream<Arguments> toEnumTest(){
        return Stream.of(
                Arguments.of("sms", VerificationType.SMS),
                Arguments.of("Sms", VerificationType.SMS),
                Arguments.of("SMS", VerificationType.SMS),
                Arguments.of("email", VerificationType.EMAIL),
                Arguments.of("Email", VerificationType.EMAIL),
                Arguments.of("EMAIL", VerificationType.EMAIL)
        );
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Enum] 일치하는 값이 없을 때 INTERNAL_SERVER_ERROR 에러 발생")
    void toEnumExceptionTest(String sut, VerificationType verificationType) throws Exception
    {
        CustomException customException =
            (CustomException)catchException(()->VerificationType.toEnum(sut));


        assertThat(customException.getErrorCode().getHttpStatus())
                .isEqualTo(customException.getErrorCode().getHttpStatus());
        assertThat(customException).hasMessage(customException.getMessage());
    }
    static Stream<Arguments> toEnumExceptionTest(){
        return Stream.of(
                Arguments.of("sm", VerificationType.SMS),
                Arguments.of("ms", VerificationType.SMS),
                Arguments.of("", VerificationType.SMS),
                Arguments.of("mail", VerificationType.EMAIL),
                Arguments.of("Emai", VerificationType.EMAIL),
                Arguments.of("", VerificationType.EMAIL)
        );
    }



}
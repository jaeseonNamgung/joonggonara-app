package com.hit.joonggonara.common.type;

import com.hit.joonggonara.common.error.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class AuthenticationTypeTest {

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Enum] 문자열 -> authenticationType 으로 변환")
    void toEnumTest(String sut, AuthenticationType authenticationType) throws Exception
    {
        AuthenticationType expectedType = AuthenticationType.toEnum(sut);

        assertThat(authenticationType).isEqualTo(expectedType);
    }
    static Stream<Arguments> toEnumTest(){
        return Stream.of(
                Arguments.of("id", AuthenticationType.ID),
                Arguments.of("Id", AuthenticationType.ID),
                Arguments.of("ID", AuthenticationType.ID),
                Arguments.of("password", AuthenticationType.PASSWORD),
                Arguments.of("PassworD", AuthenticationType.PASSWORD),
                Arguments.of("PASSWORD", AuthenticationType.PASSWORD)
        );
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("[Enum] 일치하는 값이 없을 때 INTERNAL_SERVER_ERROR 에러 발생")
    void toEnumExceptionTest(String sut) throws Exception
    {
        CustomException customException =
                (CustomException)catchException(()->AuthenticationType.toEnum(sut));


        assertThat(customException.getErrorCode().getHttpStatus())
                .isEqualTo(customException.getErrorCode().getHttpStatus());
        assertThat(customException).hasMessage(customException.getMessage());
    }
    static Stream<Arguments> toEnumExceptionTest(){
        return Stream.of(
                Arguments.of("i"),
                Arguments.of("IDD"),
                Arguments.of(""),
                Arguments.of("Passw"),
                Arguments.of("passwor"),
                Arguments.of("")
        );
    }


}
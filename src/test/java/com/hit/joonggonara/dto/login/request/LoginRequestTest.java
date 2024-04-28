package com.hit.joonggonara.dto.login.request;

import com.hit.joonggonara.dto.request.login.LoginRequest;
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

import static com.hit.joonggonara.common.properties.ValidationMessageProperties.ID_PASSWORD_NOT_BLANK;
import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private Validator sut;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        sut = factory.getValidator();
    }

    @MethodSource
    @ParameterizedTest(name = "{index} - {0}, {1}")
    @DisplayName("[API][유효성] 로그인 유효성 검증 테스트")
    void loginValidationTest(String email, String password, String message) throws Exception
    {
        //given
        LoginRequest loginRequest = LoginRequest.of(email, password);
        //when
        Set<ConstraintViolation<LoginRequest>> expectedValidate = sut.validate(loginRequest);
        //then
        assertThat(expectedValidate).isNotEmpty();
        expectedValidate.forEach(v ->{
            assertThat(v.getMessage()).isEqualTo(message);
        });
    }

    static Stream<Arguments> loginValidationTest(){
        return Stream.of(
                Arguments.of(" ", "abc1234*", ID_PASSWORD_NOT_BLANK ),
                Arguments.of("testId", " ", ID_PASSWORD_NOT_BLANK ),
                Arguments.of(null, "abc1234*", ID_PASSWORD_NOT_BLANK),
                Arguments.of("testId", null, ID_PASSWORD_NOT_BLANK),
                Arguments.of("", "", ID_PASSWORD_NOT_BLANK),
                Arguments.of(null, null, ID_PASSWORD_NOT_BLANK)
        );
    }

}
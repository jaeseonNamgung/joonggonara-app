package com.hit.joonggonara.common.util;

import com.hit.joonggonara.common.config.AwsSesConfig;
import com.hit.joonggonara.common.util.EmailUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(AwsSesConfig.class)
@SpringBootTest
class EmailUtilTest {


    @Autowired
    private EmailUtil sut;
    
    @Test
    @DisplayName("[EMAIL] 이메일 전송 테스트")
    void sendEmailTest() throws Exception
    {
        //given
        String toEmail = "sunnamgung8@naver.com";
        //when
        String expectedValue = sut.createMessage(toEmail).get();
        //then
        assertThat(expectedValue).isNotEmpty();
        assertThat(expectedValue).hasSize(6);
    }



}
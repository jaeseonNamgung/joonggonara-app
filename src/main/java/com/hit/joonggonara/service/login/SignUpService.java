package com.hit.joonggonara.service.login;


import com.hit.joonggonara.dto.request.login.SignUpRequest;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.repository.login.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SignUpService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    @Transactional
    public boolean signUp(SignUpRequest signUpRequest){
        // 이미 회원 가입 되어 있는지 확인
        if (memberRepository.existByEmail(signUpRequest.email())){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_IN_USER);
        }
        // password 인코딩
        String passwordEncode = passwordEncoder.encode(signUpRequest.password());
        memberRepository.save(signUpRequest.toEntity(passwordEncode));
        return true;
    }
}

package com.hit.joonggonara.common.custom.login;

import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.repository.login.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserIdAndLoginType(username, LoginType.GENERAL)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        return new CustomUserDetails(member);
    }
}

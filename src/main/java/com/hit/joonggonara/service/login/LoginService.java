package com.hit.joonggonara.service.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hit.joonggonara.common.custom.login.CustomUserProvider;
import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.BaseErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.GoogleProperties;
import com.hit.joonggonara.common.properties.KakaoProperties;
import com.hit.joonggonara.common.properties.NaverProperties;
import com.hit.joonggonara.common.properties.RedisProperties;
import com.hit.joonggonara.common.properties.secretConfig.GoogleSecurityConfig;
import com.hit.joonggonara.common.properties.secretConfig.KakaoSecurityConfig;
import com.hit.joonggonara.common.properties.secretConfig.NaverSecurityConfig;
import com.hit.joonggonara.common.type.AuthenticationType;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.type.VerificationType;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import com.hit.joonggonara.dto.login.OAuth2TokenDto;
import com.hit.joonggonara.dto.login.OAuth2PropertiesDto;
import com.hit.joonggonara.dto.login.TokenDto;
import com.hit.joonggonara.dto.request.login.*;
import com.hit.joonggonara.dto.response.login.FindUserIdResponse;
import com.hit.joonggonara.dto.response.login.OAuth2UserDto;
import com.hit.joonggonara.dto.response.login.TokenResponse;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.login.MemberRepository;
import com.hit.joonggonara.repository.login.condition.AuthenticationCondition;
import com.hit.joonggonara.repository.login.condition.VerificationCondition;
import com.hit.joonggonara.service.login.oidc.OidcService;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static com.hit.joonggonara.common.properties.JwtProperties.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final MemberRepository memberRepository;
    private final CustomUserProvider userProvider;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final VerificationService verificationService;
    private final OAuth2Service oAuth2Service;
    private final OidcService oidcService;
    private final GoogleSecurityConfig googleSecurityConfig;
    private final KakaoSecurityConfig kakaoSecurityConfig;
    private final NaverSecurityConfig naverSecurityConfig;


    @Transactional
    public TokenResponse login(LoginRequest loginRequest){

        // 초기 인증 정보를 넣는다.
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(loginRequest.userId(), loginRequest.password());

        Authentication authenticate = userProvider.authenticate(authentication);

        // 회원 Role을 꺼내옴
        String r = authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst()
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_EXIST_AUTHORIZATION));
        Role role = Role.checkRole(r);

        // access token, refresh token 생성
        TokenDto token = createToken((String) authenticate.getPrincipal(), role, LoginType.GENERAL);

        return TokenResponse.toResponse(token);
    }
    @Transactional
    public OAuth2UserDto oAuth2Login(String code, LoginType loginType) throws JsonProcessingException {
        OAuth2PropertiesDto oAuth2PropertiesDto = checkLoginType(loginType);
        OAuth2TokenDto oAuth2TokenDto = oAuth2Service.requestAccessToken(code, oAuth2PropertiesDto);
        Map<String, String> map;
        if(loginType.equals(LoginType.NAVER)){
            map = oAuth2Service.getUserInfoFromAccessToken(oAuth2TokenDto.access_token(), oAuth2PropertiesDto);
        }
        else{
            map = oidcService.getUserInfoFromIdToken(oAuth2TokenDto.id_token(), oAuth2PropertiesDto, loginType);

        }
        boolean existMember = memberRepository.existByEmail(map.get("email"));
        // 이미 가입된 회원인 경우 토큰 생성
        if(existMember){
            TokenDto tokenDto = createToken(map.get("email"), Role.ROLE_USER, loginType);
            return OAuth2UserDto.fromOAuth2UserDto(tokenDto, map.get("profile"));
        }
        return OAuth2UserDto.fromOAuth2UserDto(map.get("email"), map.get("profile"));
    }

    private OAuth2PropertiesDto checkLoginType(LoginType loginType) {
        if(loginType.equals(LoginType.GOGGLE)){
            return OAuth2PropertiesDto.fromDto(new GoogleProperties(googleSecurityConfig));
        } else if (loginType.equals(LoginType.NAVER)) {
            return OAuth2PropertiesDto.fromDto(new NaverProperties(naverSecurityConfig));
        } else {
            return OAuth2PropertiesDto.fromDto(new KakaoProperties(kakaoSecurityConfig));
        }
    }


    public String sendRedirect(LoginType loginType) {
        OAuth2PropertiesDto oAuth2PropertiesDto = checkLoginType(loginType);
        return oAuth2Service.sendRedirect(loginType, oAuth2PropertiesDto);
    }

    private TokenDto createToken(String principal, Role role, LoginType loginType) {
        TokenDto token = jwtUtil.createToken(principal, role, loginType);
        saveRefreshToken(principal, token.refreshToken());
        return token;
    }

    private void saveRefreshToken(String userId, String refreshToken) {

        Optional<String> optionalRefreshToken = redisUtil.get(RedisProperties.REFRESH_TOKEN_KEY + refreshToken);
        // refreshToken 이 존재 할경우 이미 로그인한 유저로 판단
        if(optionalRefreshToken.isPresent()){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_IN_USER);
        }

        redisUtil.save(
                RedisProperties.REFRESH_TOKEN_KEY + userId,
                refreshToken,
                RedisProperties.REFRESH_TOKEN_EXPIRATION_TIME
                );
    }

    // 핸드폰 인증 으로 아이디 찾기
    @Transactional
    public boolean findUserIdBySms(FindUserIdBySmsRequest findUserIdBySmsRequest){
        String name = findUserIdBySmsRequest.name();
        String phoneNumber = findUserIdBySmsRequest.phoneNumber();
        VerificationCondition condition  = VerificationCondition.of(name, phoneNumber);
        checkExistUser(condition, VerificationType.ID_SMS);
        verificationService.sendSms(phoneNumber);
        return true;
    }

    // 이메일 인증 으로 아이디 찾기
    @Transactional
    public boolean findUserIdByEmail(FindUserIdByEmailRequest findUserIdByEmailRequest){
        String name = findUserIdByEmailRequest.name();
        String email = findUserIdByEmailRequest.email();
        VerificationCondition condition  = VerificationCondition.of(name, email);
        checkExistUser(condition, VerificationType.ID_EMAIL);
        verificationService.sendEmail(email);
        return true;
    }

    // 핸드폰 인증으로 비밀번호 찾기
    @Transactional
    public boolean findPasswordBySms(FindPasswordBySmsRequest findPasswordBySmsRequest){
        String name = findPasswordBySmsRequest.name();
        String userId = findPasswordBySmsRequest.userId();
        String phoneNumber = findPasswordBySmsRequest.phoneNumber();
        VerificationCondition condition = VerificationCondition.of(name, userId, phoneNumber);
        checkExistUser(condition, VerificationType.PASSWORD_SMS);
        verificationService.sendSms(phoneNumber);

        return true;
    }

    // 이메일 인증으로 비밀번호 찾기
    @Transactional
    public boolean findPasswordByEmail(FindPasswordByEmailRequest findPasswordByEmailRequest){
        String name = findPasswordByEmailRequest.name();
        String userId = findPasswordByEmailRequest.userId();
        String email = findPasswordByEmailRequest.email();
        VerificationCondition condition = VerificationCondition.of(name, userId, email);
        checkExistUser(condition, VerificationType.PASSWORD_SMS);
        verificationService.sendEmail(email);

        return true;
    }

    private void checkExistUser(VerificationCondition condition, VerificationType verificationType) {

        boolean exist = memberRepository.existByUserNameAndVerificationTypeValue(condition, verificationType);
        // exist false라면 존재하지 않은 회원으로 판단
        if (!exist){
            throw new CustomException(UserErrorCode.NOT_EXIST_USER);
        }
    }

    public FindUserIdResponse checkUserIdVerificationCode(VerificationRequest verificationRequest, VerificationType verificationType){
        validateVerificationCode(verificationRequest, verificationType);
        AuthenticationCondition authenticationCondition =
                AuthenticationCondition.of(verificationRequest.verificationKey(), verificationType, AuthenticationType.ID);
        String userId = memberRepository.findUserIdOrPasswordByPhoneNumberOrEmail(authenticationCondition)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return FindUserIdResponse.of(userId);
    }

    public Boolean checkPasswordVerificationCode(VerificationRequest verificationRequest, VerificationType verificationType){

        validateVerificationCode(verificationRequest, verificationType);

        AuthenticationCondition authenticationCondition =
                AuthenticationCondition.of(verificationRequest.verificationKey(), verificationType, AuthenticationType.PASSWORD);
        memberRepository.findUserIdOrPasswordByPhoneNumberOrEmail(authenticationCondition)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return true;
    }


    private void validateVerificationCode(VerificationRequest verificationRequest, VerificationType verificationType){

        String verificationKey = "";
        if(VerificationType.SMS.equals(verificationType)){
            verificationKey =  RedisProperties.PHONE_NUMBER_KEY + verificationRequest.verificationKey();
        }else if(VerificationType.EMAIL.equals(verificationType)){
            verificationKey =  RedisProperties.EMAIL_KEY + verificationRequest.verificationKey();
        }else{
            throw new CustomException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }

        boolean isCheckCode = verificationService.checkVerificationCode(verificationKey, verificationRequest.verificationCode());
        if(!isCheckCode){
            throw new CustomException(UserErrorCode.VERIFICATION_CODE_MISMATCH);
        }
    }

    // 토큰 재 발급
    @Transactional
    public TokenResponse reissueToken(String token){

        if(!Strings.hasText(token)){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_OUT_USER);
        }

        String principal = jwtUtil.getPrincipal(token);
        Role role = jwtUtil.getRole(token);

        redisUtil.get(RedisProperties.REFRESH_TOKEN_KEY + principal)
                .orElseThrow(() -> new CustomException(UserErrorCode.ALREADY_LOGGED_OUT_USER));


        TokenDto tokenDto = jwtUtil.createToken(principal, role, LoginType.GENERAL);

        // 기존 Refresh Token 제거
        redisUtil.delete(RedisProperties.REFRESH_TOKEN_KEY + principal);
        
        // 새로 발급 받은 Refresh Token 추가
        redisUtil.save(
                RedisProperties.REFRESH_TOKEN_KEY + principal,
                tokenDto.refreshToken(),
                RedisProperties.REFRESH_TOKEN_EXPIRATION_TIME
        );
        return TokenResponse.toResponse(tokenDto);
    }

    @Transactional
    public boolean withdrawal(HttpServletRequest request){
        String jwtToken = request.getHeader(AUTHORIZATION);
        String accessToken = getParseJwt(jwtToken);
        if(accessToken == null){
            throw new CustomException(UserErrorCode.ALREADY_LOGGED_OUT_USER);
        }

        String principal = jwtUtil.getPrincipal(accessToken);
        LoginType loginType = jwtUtil.getLoginType(accessToken);

        // 로그인 타입 별로 member 삭제
        if(loginType.equals(LoginType.GENERAL)){
            memberRepository.deleteByUserId(principal);
        }else{
            memberRepository.deleteByEmail(principal);
        }
        
        redisUtil.delete(REFRESH_TOKEN_NAME + principal);
        return true;
    }

    @Transactional
    public void memberUpdateInfo(String token, MemberUpdateRequest memberUpdateRequest) {
        String parseJwt = getParseJwt(token);
        String principal = jwtUtil.getPrincipal(parseJwt);
        LoginType loginType = jwtUtil.getLoginType(parseJwt);
        Member member = memberRepository.findByPrincipalAndLoginType(principal, loginType)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        member.update(memberUpdateRequest);
    }
    private String getParseJwt(String token) {
        if(Strings.hasText(token) && token.startsWith(JWT_TYPE)){
            return token.substring(7);
        }
        return null;
    }



}

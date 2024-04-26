package com.hit.joonggonara.util;

import com.hit.joonggonara.dto.TokenDto;
import com.hit.joonggonara.error.CustomException;
import com.hit.joonggonara.error.errorCode.UserErrorCode;
import com.hit.joonggonara.properties.JwtProperties;
import com.hit.joonggonara.type.LoginType;
import com.hit.joonggonara.type.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key SECRET_KEY;

    public JwtUtil(
            @Value("${jwt.secret.key}")
            String SECRET_KEY) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET_KEY.getBytes()));
    }


    public TokenDto getToken(String email, Role role, LoginType loginType){
        long accessTokenPeriod = 1000L * 60L * 30L; // 30분
        long refreshTokenPeriod = 1000L * 60L * 60L * 24L * 14; // 2주

        String accessToken = createAccessToken(email, role, loginType, accessTokenPeriod);
        String refreshToken = createRefreshToken(email, role, loginType,refreshTokenPeriod);
        return TokenDto.of(accessToken, refreshToken, email);
    }

    private String createRefreshToken(String email, Role role, LoginType loginType, long refreshTokenPeriod) {
        Date now = new Date();
        return Jwts.builder().setSubject(JwtProperties.REFRESH_TOKEN_NAME)
                .setHeader(createHeader())
                .setClaims(createClaims(email, role, loginType))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    private String createAccessToken(String email, Role role, LoginType loginType, long accessTokenPeriod) {
        Date now = new Date();

        return Jwts.builder().setSubject(JwtProperties.ACCESS_TOKEN_NAME)
                .setHeader(createHeader())
                .setClaims(createClaims(email, role, loginType))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenPeriod))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();

    }

    private Claims createClaims(String email, Role role, LoginType loginType) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("role", role);
        claims.put("loginType", loginType);
        return claims;
    }

    private Map<String, Object> createHeader() {
        HashMap<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }

    // token 유효성 검사
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        }catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new CustomException(UserErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(UserErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(UserErrorCode.UNSUPPORTED_TOKEN);
        }
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token)
                .getBody().get("email").toString();
    }

    public Role getRole(String token) {
        String role = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token)
                .getBody().get("role").toString();
        return Role.checkRole(role);
    }
}

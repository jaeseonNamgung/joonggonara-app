package com.hit.joonggonara.common.util;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.type.TokenType;
import com.hit.joonggonara.dto.login.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtil {

    private final Key SECRET_KEY;
    private final static String RSA = "RSA";
    private final static String KID = "kid";
    public JwtUtil(
            @Value("${jwt.secret.key}")
            String SECRET_KEY) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET_KEY.getBytes()));
    }


    public TokenDto createToken(String principal, Role role, LoginType loginType){
        long accessTokenPeriod = 1000L * 60L * 60L * 24L * 14; // 30분
        long refreshTokenPeriod = 1000L * 60L * 60L * 24L * 14; // 2주

        String accessToken = createAccessToken(principal, role, loginType, accessTokenPeriod);
        String refreshToken = createRefreshToken(principal, role, loginType,refreshTokenPeriod);
        return TokenDto.of(accessToken, refreshToken, principal);
    }

    private String createRefreshToken(String principal, Role role, LoginType loginType, long refreshTokenPeriod) {
        Date now = new Date();
        return Jwts.builder().setSubject(JwtProperties.REFRESH_TOKEN_NAME)
                .setHeader(createHeader())
                .setClaims(createClaims(principal, role, loginType))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenPeriod))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    private String createAccessToken(String principal, Role role, LoginType loginType, long accessTokenPeriod) {
        Date now = new Date();

        return Jwts.builder().setSubject(JwtProperties.ACCESS_TOKEN_NAME)
                .setHeader(createHeader())
                .setClaims(createClaims(principal, role, loginType))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenPeriod))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();

    }

    private Claims createClaims(String principal, Role role, LoginType loginType) {
        Claims claims = Jwts.claims();
        claims.put("principal", principal);
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
    public boolean validateToken(String token, TokenType tokenType){
        try{

            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        }catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new CustomException(UserErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            if(tokenType.equals(TokenType.ACCESS_TOKEN)){
                throw new CustomException(UserErrorCode.EXPIRED_TOKEN);
            }else{
                throw new CustomException(UserErrorCode.REFRESH_TOKEN_EXPIRED_TOKEN);
            }

        } catch (UnsupportedJwtException e) {
            throw new CustomException(UserErrorCode.UNSUPPORTED_TOKEN);
        }
    }

    private Jwt<Header, Claims> validateIdToken(String token, String issuer, String audience){
        try{
            return Jwts.parserBuilder()
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseClaimsJwt(getUnsignedToken(token));
        }catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new CustomException(UserErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(UserErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(UserErrorCode.UNSUPPORTED_TOKEN);
        }
    }

    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");

        if(splitToken.length != 3){
            throw new CustomException(UserErrorCode.INVALID_TOKEN);
        }

        return splitToken[0]  +  "." + splitToken[1] + ".";
    }

    private Jws<Claims> validationIdToken(String token, String modulus, String exponent) {
        try{
            token = token.replace("—", "--");
            return Jwts.parserBuilder().setSigningKey(getRSAPublicKey(modulus, exponent)).build().parseClaimsJws(token);
        }catch (SecurityException | MalformedJwtException | IllegalArgumentException | InvalidKeySpecException e) {
            throw new CustomException(UserErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(UserErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(UserErrorCode.UNSUPPORTED_TOKEN);
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(UserErrorCode.NO_SUCH_ALGORITHM);
        }
    }

    private Key getRSAPublicKey(String modulus, String exponent) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(RSA);

        byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);

        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
        return keyFactory.generatePublic(rsaPublicKeySpec);

    }

    public String getPrincipal(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token)
                .getBody().get("principal").toString();
    }

    public Role getRole(String token) {
        String role = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token)
                .getBody().get("role").toString();
        return Role.checkRole(role);
    }

    public LoginType getLoginType(String token){
        String loginType = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token)
                .getBody().get("loginType").toString();
        return LoginType.checkType(loginType);
    }

    public Long getExpired(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    public String getKid(String token, String issuer, String audience) {
            return (String) validateIdToken(token, issuer, audience).getHeader().get(KID);
    }

    public Map<String, String> getOidcTokenBody(String token, String modulus, String exponent) {
        Claims claims = validationIdToken(token, modulus, exponent).getBody();
        return Map.of("email", String.valueOf(claims.get("email")), "profile", String.valueOf(claims.get("picture")));
    }

}

package com.hit.joonggonara.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.common.custom.login.CustomAccessDeniedHandler;
import com.hit.joonggonara.common.custom.login.CustomAuthenticationEntryPoint;
import com.hit.joonggonara.common.custom.login.CustomExceptionFilter;
import com.hit.joonggonara.common.custom.login.CustomJwtFilter;
import com.hit.joonggonara.common.properties.JwtProperties;
import com.hit.joonggonara.common.util.JwtUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request->{
                    request.requestMatchers("/", "/ws/**", "/css/**","/js/**", "/favicon.ico",
                                     "/user/login","/user/login/reissue", "/user/login/**", "/user/signUp",
                                    "/user/signUp/**","/user/social/signUp" ,"/board/search/list", "/board/search").permitAll()
                            .anyRequest().authenticated();
                })
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(authenticationEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler);
                })
                .addFilterBefore(new CustomJwtFilter(jwtUtil, redisUtil),UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomExceptionFilter(objectMapper),CustomJwtFilter.class)
                .passwordManagement(httpSecurityPasswordManagementConfigurer -> passwordEncoder());
        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:9090", "http://localhost:8081"));
        corsConfiguration.addExposedHeader(JwtProperties.AUTHORIZATION);
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource
                 = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
       return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring().requestMatchers("/user/login/reissue");
        };
    }
}

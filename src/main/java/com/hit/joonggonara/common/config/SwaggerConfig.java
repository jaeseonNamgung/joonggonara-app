package com.hit.joonggonara.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info= @Info(
                // api 제목
                title = "GoodByeGood Api",
                // api 설명
                description = "GoodByeGood RestApi 설정",
                // api 버전
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {
    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    public OpenAPI openAPI(){
        String securityJwtName = "JWT";
        // API가 JWT 인증을 요구한다는 것을 나타냄
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);

        // Swagger에서 사용하는 보안 스키마(인증 방식)를 정의
        // SecurityScheme.Type.HTTP 타입의 Bearer 토큰 인증 방식을 사용하며, Bearer 포맷으로 JWT를 지정
        Components components = new Components()
                .addSecuritySchemes(securityJwtName, new SecurityScheme()
                        .name(securityJwtName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIX)
                        .bearerFormat(securityJwtName)
                );
        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}

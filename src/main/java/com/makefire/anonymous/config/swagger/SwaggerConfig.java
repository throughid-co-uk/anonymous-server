package com.makefire.anonymous.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 *packageName : com.makefire.anonymous
 * fileName : SwaggerConfig
 * author : 최푸름
 * date : 22-01-14
 * description : 스웨거 설정 클래스
 * =================================
 * DATE     AUTHOR   NOTE
 * 22-01-15 최푸름
 * ---------------------------------
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("Anonymous API")
                .description("side toy project")
                .version("1.0"));
    }
}
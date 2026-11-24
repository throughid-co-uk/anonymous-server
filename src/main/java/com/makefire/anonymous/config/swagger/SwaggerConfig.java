package com.makefire.anonymous.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    /** Compatibility discovery endpoints for clients of the former Springfox UI. */
    @org.springframework.web.bind.annotation.RestController
    public static class DocumentationDiscovery {
        @org.springframework.web.bind.annotation.GetMapping("/swagger-resources")
        public java.util.List<?> resources() {
            return java.util.List.of(java.util.Map.of("name", "default", "url", "/v3/api-docs",
                    "swaggerVersion", "3.0.3", "location", "/v3/api-docs"));
        }

        @org.springframework.web.bind.annotation.GetMapping({"/swagger-resources/configuration/security",
                "/swagger-resources/configuration/ui"})
        public java.util.Map<?, ?> configuration() {
            return java.util.Map.of();
        }

        @org.springframework.web.bind.annotation.GetMapping("/v2/api-docs")
        public org.springframework.web.servlet.ModelAndView legacyDocs() {
            return new org.springframework.web.servlet.ModelAndView("forward:/v3/api-docs");
        }
    }

    @Bean
    public io.swagger.v3.oas.models.OpenAPI api() {
        return new io.swagger.v3.oas.models.OpenAPI().info(apiInfo());
    }

    private io.swagger.v3.oas.models.info.Info apiInfo() {
        return new io.swagger.v3.oas.models.info.Info()
                .title("Anonymous API")
                .description("side toy project")
                .version("1.0");
    }
}
package com.makefire.anonymous.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.Ordered;

/** Translates configuration values removed by Spring Boot 4 and Hibernate 7. */
public class FrameworkCompatibilityEnvironment implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> settings = new LinkedHashMap<>();
        String matching = "spring.mvc.pathmatch.matching-strategy";
        if ("ant_path_matcher".equals(environment.getProperty(matching))) {
            settings.put(matching, "path_pattern_parser");
        }
        String dialect = "spring.jpa.properties.hibernate.dialect";
        if ("org.hibernate.dialect.MySQL5Dialect".equals(environment.getProperty(dialect))) {
            settings.put(dialect, "org.hibernate.dialect.H2Dialect");
        }
        settings.put("spring.jpa.properties.hibernate.auto_quote_keyword", true);
        settings.put("spring.jackson.deserialization.fail-on-null-for-primitives", false);
        settings.put("spring.jackson.deserialization.fail-on-trailing-tokens", false);
        settings.put("spring.jackson.serialization.indent-output", false);
        settings.put("spring.jackson.mapper.sort-properties-alphabetically", false);
        settings.put("spring.servlet.encoding.enabled", false);
        environment.getPropertySources().addFirst(new MapPropertySource("frameworkCompatibility", settings));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}

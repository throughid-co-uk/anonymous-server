package com.makefire.anonymous.config.web;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/** Translates obsolete Boot 2 settings before Boot 4 binds configuration. */
public class LegacyConfigurationMigration implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> settings = new HashMap<>();
        if ("ant_path_matcher".equals(environment.getProperty("spring.mvc.pathmatch.matching-strategy"))) {
            settings.put("spring.mvc.pathmatch.matching-strategy", "path-pattern-parser");
        }
        String url = environment.getProperty("spring.datasource.hikari.jdbc-url",
                environment.getProperty("spring.datasource.url"));
        if ("org.hibernate.dialect.MySQL5Dialect".equals(environment.getProperty("spring.jpa.properties.hibernate.dialect"))) {
            settings.put("spring.jpa.properties.hibernate.dialect",
                    url != null && url.startsWith("jdbc:h2:")
                            ? "org.hibernate.dialect.H2Dialect"
                            : "org.hibernate.dialect.MySQLDialect");
        }
        if (url != null && url.startsWith("jdbc:h2:") && !url.contains("NON_KEYWORDS=")) {
            String urlProperty = environment.containsProperty("spring.datasource.hikari.jdbc-url")
                    ? "spring.datasource.hikari.jdbc-url" : "spring.datasource.url";
            settings.put(urlProperty, url + ";NON_KEYWORDS=USER");
        }
        settings.put("spring.jackson.use-jackson2-defaults", true);
        settings.put("spring.jackson.serialization.indent-output", false);
        settings.put("server.servlet.encoding.force-response", false);
        settings.put("spring.jackson.deserialization.fail-on-null-for-primitives", false);
        settings.put("spring.jackson.deserialization.fail-on-trailing-tokens", false);
        environment.getPropertySources().addFirst(new MapPropertySource("legacyConfigurationMigration", settings));
    }
}

package com.makefire.anonymous.config.web;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class LegacyConfigurationMigrationTest {
    private static final String DIALECT = "spring.jpa.properties.hibernate.dialect";
    private static final String HIKARI_URL = "spring.datasource.hikari.jdbc-url";
    private static final String URL = "spring.datasource.url";

    private MockEnvironment migrate(Map<String, String> properties) {
        MockEnvironment environment = new MockEnvironment();
        properties.forEach(environment::setProperty);
        new LegacyConfigurationMigration().postProcessEnvironment(environment, new SpringApplication());
        return environment;
    }

    @Test
    void translatesLegacyDialectForMySql() {
        for (String property : new String[]{URL, HIKARI_URL}) {
            String url = "jdbc:mysql://localhost/anonymous";
            MockEnvironment environment = migrate(Map.of(DIALECT, "org.hibernate.dialect.MySQL5Dialect", property, url));
            assertThat(environment.getProperty(DIALECT)).isEqualTo("org.hibernate.dialect.MySQLDialect");
            assertThat(environment.getProperty(property)).isEqualTo(url);
        }
    }

    @Test
    void onlySelectsH2ForAnH2Datasource() {
        for (String property : new String[]{URL, HIKARI_URL}) {
            MockEnvironment environment = migrate(Map.of(DIALECT, "org.hibernate.dialect.MySQL5Dialect",
                    property, "jdbc:h2:mem:testdb;MODE=MYSQL"));
            assertThat(environment.getProperty(DIALECT)).isEqualTo("org.hibernate.dialect.H2Dialect");
            assertThat(environment.getProperty(property)).endsWith(";NON_KEYWORDS=USER");
        }
    }

    @Test
    void doesNotAssumeH2WhenUrlIsAbsent() {
        assertThat(migrate(Map.of(DIALECT, "org.hibernate.dialect.MySQL5Dialect")).getProperty(DIALECT))
                .isEqualTo("org.hibernate.dialect.MySQLDialect");
    }

    @Test
    void preservesExplicitSupportedDialectAndAutoDetection() {
        assertThat(migrate(Map.of(DIALECT, "org.hibernate.dialect.PostgreSQLDialect")).getProperty(DIALECT))
                .isEqualTo("org.hibernate.dialect.PostgreSQLDialect");
        assertThat(migrate(Map.of(URL, "jdbc:mysql://localhost/anonymous")).getProperty(DIALECT)).isNull();
    }
}

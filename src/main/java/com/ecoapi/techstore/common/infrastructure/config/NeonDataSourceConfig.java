package com.ecoapi.techstore.common.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Neon PostgreSQL DataSource Configuration.
 * 
 * Follows Neon best practices:
 * - Runtime web queries use pooled connection (-pooler endpoint via DATABASE_URL).
 * - Schema migrations with Flyway use direct connection (unpooled endpoint via DATABASE_URL_UNPOOLED).
 */
@Configuration
@ConditionalOnClass({DataSource.class, HikariDataSource.class})
@Conditional(NeonDataSourceConfig.NeonCondition.class)
public class NeonDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(NeonDataSourceConfig.class);

    public static class NeonCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment env = context.getEnvironment();
            String dbUrl = env.getProperty("DATABASE_URL");
            String unpooledUrl = env.getProperty("DATABASE_URL_UNPOOLED");
            return (dbUrl != null && !dbUrl.isBlank()) || (unpooledUrl != null && !unpooledUrl.isBlank());
        }
    }

    public record ParsedDbConfig(String jdbcUrl, String username, String password) {}

    public static ParsedDbConfig parsePostgresUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }
        String cleaned = rawUrl.trim();
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        if (cleaned.startsWith("'") && cleaned.endsWith("'") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        String uriString = cleaned;
        if (uriString.startsWith("jdbc:postgresql://")) {
            uriString = uriString.substring(5);
        }
        URI uri = URI.create(uriString);
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = uri.getPath() != null && !uri.getPath().isEmpty() ? uri.getPath() : "/neondb";
        String query = uri.getQuery() != null && !uri.getQuery().isEmpty() ? "?" + uri.getQuery() : "";
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + query;
        String username = null;
        String password = null;
        if (uri.getUserInfo() != null) {
            String[] parts = uri.getUserInfo().split(":", 2);
            username = parts[0];
            if (parts.length > 1) {
                password = parts[1];
            }
        }
        return new ParsedDbConfig(jdbcUrl, username, password);
    }

    @Primary
    @Bean
    public DataSource dataSource(
            @Value("${DATABASE_URL:${DATABASE_URL_UNPOOLED:}}") String databaseUrl,
            @Value("${DB_POOL_MAX_SIZE:10}") int maxPoolSize,
            @Value("${DB_POOL_MIN_IDLE:2}") int minIdle,
            @Value("${DB_CONNECTION_TIMEOUT:30000}") long connectionTimeout) {
        log.info("Configuring Neon pooled primary DataSource from DATABASE_URL");
        ParsedDbConfig config = parsePostgresUrl(databaseUrl);
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(config.jdbcUrl());
        if (config.username() != null) {
            ds.setUsername(config.username());
        }
        if (config.password() != null) {
            ds.setPassword(config.password());
        }
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setMaximumPoolSize(maxPoolSize);
        ds.setMinimumIdle(minIdle);
        ds.setConnectionTimeout(connectionTimeout);
        ds.setPoolName("NeonHikariPool");
        return ds;
    }

    @Bean
    @FlywayDataSource
    public DataSource flywayDataSource(
            @Value("${DATABASE_URL_UNPOOLED:${DATABASE_URL:}}") String migrationUrl) {
        log.info("Configuring Neon direct Flyway DataSource from DATABASE_URL_UNPOOLED");
        ParsedDbConfig config = parsePostgresUrl(migrationUrl);
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(config.jdbcUrl());
        if (config.username() != null) {
            ds.setUsername(config.username());
        }
        if (config.password() != null) {
            ds.setPassword(config.password());
        }
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setMaximumPoolSize(2);
        ds.setMinimumIdle(1);
        ds.setConnectionTimeout(30000);
        ds.setPoolName("NeonFlywayPool");
        return ds;
    }
}

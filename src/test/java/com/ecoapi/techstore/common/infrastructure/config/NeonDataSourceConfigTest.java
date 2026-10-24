package com.ecoapi.techstore.common.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NeonDataSourceConfigTest {

    @Test
    void parsePostgresUrlHandlesPooledUrlWithQuotes() {
        String raw = "\"postgresql://neondb_owner:npg_secret123@ep-lively-glitter-b59up623-pooler.c-7.us-east-2.aws.neon.tech/neondb?channel_binding=require&sslmode=require\"";
        NeonDataSourceConfig.ParsedDbConfig config = NeonDataSourceConfig.parsePostgresUrl(raw);

        assertThat(config).isNotNull();
        assertThat(config.jdbcUrl()).isEqualTo("jdbc:postgresql://ep-lively-glitter-b59up623-pooler.c-7.us-east-2.aws.neon.tech:5432/neondb?channel_binding=require&sslmode=require");
        assertThat(config.username()).isEqualTo("neondb_owner");
        assertThat(config.password()).isEqualTo("npg_secret123");
    }

    @Test
    void parsePostgresUrlHandlesDirectUnpooledUrlWithoutQuotes() {
        String raw = "postgresql://neondb_owner:npg_secret123@ep-lively-glitter-b59up623.c-7.us-east-2.aws.neon.tech/neondb?channel_binding=require&sslmode=require";
        NeonDataSourceConfig.ParsedDbConfig config = NeonDataSourceConfig.parsePostgresUrl(raw);

        assertThat(config).isNotNull();
        assertThat(config.jdbcUrl()).isEqualTo("jdbc:postgresql://ep-lively-glitter-b59up623.c-7.us-east-2.aws.neon.tech:5432/neondb?channel_binding=require&sslmode=require");
        assertThat(config.username()).isEqualTo("neondb_owner");
        assertThat(config.password()).isEqualTo("npg_secret123");
    }

    @Test
    void parsePostgresUrlHandlesStandardJdbcPrefix() {
        String raw = "jdbc:postgresql://neondb_owner:npg_secret123@ep-direct.c-7.neon.tech:5432/neondb?sslmode=require";
        NeonDataSourceConfig.ParsedDbConfig config = NeonDataSourceConfig.parsePostgresUrl(raw);

        assertThat(config).isNotNull();
        assertThat(config.jdbcUrl()).isEqualTo("jdbc:postgresql://ep-direct.c-7.neon.tech:5432/neondb?sslmode=require");
        assertThat(config.username()).isEqualTo("neondb_owner");
        assertThat(config.password()).isEqualTo("npg_secret123");
    }

    @Test
    void parsePostgresUrlHandlesBlankReturnsNull() {
        assertThat(NeonDataSourceConfig.parsePostgresUrl(null)).isNull();
        assertThat(NeonDataSourceConfig.parsePostgresUrl("   ")).isNull();
    }
}

package com.datatool.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.example.datatool.model.ClickHouseConfig;
import com.example.datatool.service.ClickHouseService;

class ClickHouseConfigTest {

    @Test
    void shouldDetectHttpsPorts() {
        ClickHouseConfig config = new ClickHouseConfig();
        config.setPort(8443);
        assertTrue(config.isSecure());

        config.setPort(9000);
        assertFalse(config.isSecure());
    }

    @Test
    void shouldHandleJwtToken() {
        ClickHouseConfig config = new ClickHouseConfig();
        config.setJwtToken("test.jwt.token");
        assertEquals("test.jwt.token", config.getJwtToken());
    }
    @Test
    void shouldBuildValidJdbcUrl() {
        ClickHouseConfig config = new ClickHouseConfig();
        config.setHost("clickhouse.prod");
        config.setPort(9440);
        config.setDatabase("analytics");

        String expectedUrl = "jdbc:clickhouse://clickhouse.prod:9440/analytics?ssl=true";
        assertTrue(new ClickHouseService().buildJdbcUrl(config).contains(expectedUrl));
    }
}
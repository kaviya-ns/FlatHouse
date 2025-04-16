package com.example.datatool.service;

import com.example.datatool.model.ClickHouseConfig;
import org.springframework.stereotype.Service;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
@Service
public class ClickHouseService {

    public Connection createConnection(ClickHouseConfig config) throws SQLException {
        String url = String.format(
            "jdbc:clickhouse://%s:%d/%s?ssl=%b",
            config.getHost(),
            config.getPort(),
            config.getDatabase(),
            config.isSecure()
        );
        
        Properties props = new Properties();
        props.setProperty("user", config.getUser());
        props.setProperty("password", config.getJwtToken());  // JWT passed here
        
        return DriverManager.getConnection(url, props);
    }

    public List<String> fetchTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }
    public String buildJdbcUrl(ClickHouseConfig config) {
        boolean isSecure = config.getPort() == 8443 || config.getPort() == 9440;
        String sslParam = isSecure ? "?ssl=true" : "";
        
        return String.format(
            "jdbc:clickhouse://%s:%d/%s%s",
            config.getHost(),
            config.getPort(),
            config.getDatabase(),
            sslParam
        );
    }
}
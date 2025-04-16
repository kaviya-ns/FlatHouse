package com.example.datatool.model;

import lombok.Data;

@Data
public class ClickHouseConfig {
    private String host = "localhost";
    private int port = 8123;
    private String database = "default";
    private String user = "default";
    private String jwtToken;  // For JWT auth

    public boolean isSecure() {
        return port == 8443 || port == 9440;  // HTTPS ports
    }
}
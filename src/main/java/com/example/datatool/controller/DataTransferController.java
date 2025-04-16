package com.example.datatool.controller;
import com.example.datatool.model.ClickHouseConfig;
import com.example.datatool.model.TableColumn;    
import com.example.datatool.service.ClickHouseService;
import org.springframework.web.bind.annotation.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.FileWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class DataTransferController {

    private final ClickHouseService service;

    public DataTransferController(ClickHouseService service) {
        this.service = service;
    }
    
    //connect to clickhouse
    @PostMapping("/connect")
    public Map<String, Object> connect(@RequestBody ClickHouseConfig config) {
        Map<String, Object> response = new HashMap<>();
        try (Connection conn = service.createConnection(config)) {
            response.put("status", "success");
            response.put("tables", service.fetchTables(conn));
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Authentication failed: " + e.getMessage());
        }
        return response;
    }

    // Get columns for a table
    @PostMapping("/get-columns")
    public Map<String, Object> getColumns(@RequestParam String table, @RequestBody ClickHouseConfig config) {
        Map<String, Object> response = new HashMap<>();
        try (Connection conn = getConnection(config)) {
            List<TableColumn> columns = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("DESCRIBE TABLE " + table)) {
                while (rs.next()) {
                    TableColumn col = new TableColumn();
                    col.setName(rs.getString(1));
                    col.setType(rs.getString(2));
                    columns.add(col);
                }
            }
            response.put("status", "success");
            response.put("columns", columns);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    // Export ClickHouse table → CSV
    @PostMapping("/export")
    public Map<String, Object> exportToCsv(
            @RequestParam String table,
            @RequestParam List<String> columns,
            @RequestBody ClickHouseConfig config) {
        Map<String, Object> response = new HashMap<>();
        String filename = table + "_export.csv";
        
        try (Connection conn = getConnection(config);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + String.join(",", columns) + " FROM " + table);
             FileWriter writer = new FileWriter(filename);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(columns.toArray(new String[0])))) {
            
            int recordCount = 0;
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (String col : columns) {
                    row.add(rs.getObject(col));
                }
                csvPrinter.printRecord(row);
                recordCount++;
            }
            
            response.put("status", "success");
            response.put("filename", filename);
            response.put("recordCount", recordCount);
        } 
        catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    // Helper: Create ClickHouse connection
    private Connection getConnection(ClickHouseConfig config) throws SQLException {
        String url = String.format("jdbc:clickhouse://%s:%d/%s?user=%s&password=%s",
                config.getHost(),
                config.getPort(),
                config.getDatabase(),
                config.getUser(),
                config.getJwtToken());
        return DriverManager.getConnection(url);
    }
}
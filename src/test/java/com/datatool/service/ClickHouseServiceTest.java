package com.datatool.service;

import com.example.datatool.model.ClickHouseConfig;
import com.example.datatool.service.ClickHouseService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.sql.*;
import static org.mockito.Mockito.*;

class ClickHouseServiceTest {

    @Test
    void testFetchTables() throws SQLException {
        // 1. Setup mock objects
        ClickHouseConfig config = new ClickHouseConfig();
        Connection mockConn = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        ResultSet mockRs = mock(ResultSet.class);
        
        // 2. Define mock behavior
        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery("SHOW TABLES")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getString(1)).thenReturn("test_table");

        // 3. Test service method
        ClickHouseService service = new ClickHouseService();
        var tables = service.fetchTables(mockConn);
        
        // 4. Verify
        assertEquals(1, tables.size());
        assertEquals("test_table", tables.get(0));
    }
}
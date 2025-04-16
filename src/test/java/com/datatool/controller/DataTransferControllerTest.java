package com.datatool.controller;

import com.example.datatool.service.ClickHouseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.datatool.controller.DataTransferController;

import java.sql.Connection;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataTransferController.class)
public class DataTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClickHouseService service;

    @Test
    void connect_WithValidJWT_ShouldSucceed() throws Exception {
        Mockito.when(service.createConnection(any())).thenReturn(Mockito.mock(Connection.class));
        Mockito.when(service.fetchTables(any())).thenReturn(List.of("table1", "table2"));

        String validRequest = "{\n" +
    "    \"host\": \"localhost\",\n" + 
    "    \"port\": 8443,\n" +
    "    \"user\": \"default\",\n" +
    "    \"jwtToken\": \"test.valid.jwt.token\"\n" +
    "}";

        mockMvc.perform(post("/api/connect")
                .contentType("application/json")
                .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}
package com.example.datatool.model;   // Match your package structure

import lombok.Data;

@Data  // Lombok annotation for getters/setters
public class TableColumn {
    private String name;
    private String type;
}
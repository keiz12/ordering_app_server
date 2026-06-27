package com.example.orderingapp.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private HashMap<String, String> imagePathToDeletePath = new HashMap<>();
    private List<String> imageURLPath = new ArrayList<>();
}

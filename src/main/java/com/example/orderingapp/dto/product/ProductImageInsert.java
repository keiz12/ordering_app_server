package com.example.orderingapp.dto.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductImageInsert {
    private String productName;
    private List<String> productImagePaths;
}

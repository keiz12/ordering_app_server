package com.example.orderingapp.dto.product;

import lombok.Data;

@Data
public class ProductUpdateRequest {
    private String oldProductName;
    private ProductDTO newProduct;
}

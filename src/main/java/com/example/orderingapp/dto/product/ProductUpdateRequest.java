package com.example.orderingapp.dto.product;

import lombok.Data;

@Data
public class ProductUpdateRequest {
    private ProductDTO oldProduct;
    private ProductDTO newProduct;
}

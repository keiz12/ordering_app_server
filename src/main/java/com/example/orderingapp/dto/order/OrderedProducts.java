package com.example.orderingapp.dto.order;

import com.example.orderingapp.dto.product.ProductDTO;
import lombok.Data;

@Data
public class OrderedProducts {
    private ProductDTO productDTO;
    private int productQuantity;
}

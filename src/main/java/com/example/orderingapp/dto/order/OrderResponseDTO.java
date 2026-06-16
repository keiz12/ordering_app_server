package com.example.orderingapp.dto.order;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long id;
    private String uuid;
    private Integer tableNumber;
    private Boolean orderPaid;
    private LocalDateTime createdAt;
    private List<OrderedProducts> orderedProducts;
}

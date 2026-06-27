package com.example.orderingapp.dto.order;

import lombok.*;

import java.time.*;

@Data
public class StaffProcessedOrder {

    private Order order;
    private String processedBy;
    private LocalDateTime processedAt;
}

package com.example.orderingapp.dto.order;

import lombok.*;

@Data
public class OrderProcessIndicator {
    private String uuid;
    private boolean isProcessed;
}

package com.example.orderingapp.dto.order;

import lombok.*;

import java.math.*;
import java.time.*;
import java.util.*;

@Data
public class Order {

    private long id;

    private HashMap<String, Integer> productNameToQty = new HashMap<>();

    private HashMap<String, BigDecimal> productNameToPrice = new HashMap<>();

    private int tableNumber;

    private String uuid;

    private boolean isPaid;

    private String apiKey;

    private LocalDateTime createdAt;
}

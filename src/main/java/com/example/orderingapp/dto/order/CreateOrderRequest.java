package com.example.orderingapp.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    private Integer tableNumber;

    private String uuid;

    private List<OrderedProducts> orderedProducts;

    private String apiKey;
}
/*
* create a method to iterate through the private List<OrderedProducts> orderedProducts and compare them to the List<OrderedProducts> orderedProducts u got from the database
* */
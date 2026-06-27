package com.example.orderingapp.dto.statistic;

import lombok.*;

import java.math.*;
import java.util.*;

@Data
public class StatisticResponse {

    private final Map<String, BigDecimal> rawDataMap = new TreeMap<>();

    private final Map<String, BigDecimal> percentageDataMap = new TreeMap<>();

    private final Map<String, Double> ratings = new TreeMap<>();
}

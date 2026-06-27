package com.example.orderingapp.dto.statistic;

import lombok.*;

@Data
public class StatisticRequest {

    private String startingDate;

    private String endingDate;

    private StatisticType statisticType;
}

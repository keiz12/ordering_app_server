package com.example.orderingapp.statistic;

import com.example.orderingapp.dto.statistic.*;

import java.math.*;
import java.util.*;

public class StatisticCalculator {

    public void populateStatisticsResponse (StatisticResponse statisticResponse)
    {
        BigDecimal totalValues = getValuesInTotal(statisticResponse.getRawDataMap().values());

        populatePercentageMap(statisticResponse.getRawDataMap(), statisticResponse.getPercentageDataMap(), totalValues);

        populateRatings(statisticResponse.getPercentageDataMap(), statisticResponse.getRatings());
    }

    private BigDecimal getValuesInTotal (Collection<BigDecimal> decimals) {
        return decimals.stream().reduce(new BigDecimal(0), (identity, elt) -> identity.add(elt));
    }

    private void populatePercentageMap (Map<String, BigDecimal> rawMap, Map<String, BigDecimal> percentageMap, BigDecimal total)
    {
        for (Map.Entry<String, BigDecimal> set : rawMap.entrySet()) {

            BigDecimal value = set.getValue();

            BigDecimal rawQuotient = value.divide(total,2, RoundingMode.HALF_UP);

            BigDecimal percentageQuotient = rawQuotient.multiply(new BigDecimal(100));

            percentageMap.put(set.getKey(), percentageQuotient);
        }
    }

    private void populateRatings ( Map<String, BigDecimal> percentageMap, Map<String, Double> ratingsMap)
    {
        ratingsMap.putAll(Map.of("best",0.0,"mid",0.0,"worst",0.0));

//        int best=0, mid=0, worst=0;

        for (Map.Entry<String, BigDecimal> set : percentageMap.entrySet())
        {
            int best = set.getValue().compareTo(new BigDecimal(70)); // less equal greater
            int mid = set.getValue().compareTo(new BigDecimal(50));

            if (best == 1 || best == 0)
                ratingsMap.replace("best", ratingsMap.get("best")+1);

            else if (mid == 1 || mid == 0)
                ratingsMap.replace("mid", ratingsMap.get("mid")+1);

            else
                ratingsMap.replace("worst", ratingsMap.get("worst")+1);
        }

        for (Map.Entry<String, Double> entry : ratingsMap.entrySet())
        {
            ratingsMap.replace(entry.getKey(), (entry.getValue()/ percentageMap.size() )*100);
        }
    }
}

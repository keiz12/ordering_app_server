package com.example.orderingapp.statistic;

import com.example.orderingapp.dto.statistic.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class StatisticService {



    private final StatisticRepository statisticRepository;

    public ResponseEntity<?> getRevenueStatistics (StatisticRequest statisticRequest)
    {
        StatisticResponse statisticResponse = new StatisticResponse();

        statisticRepository.getRevenueMap(statisticRequest, statisticResponse);

        new StatisticCalculator().populateStatisticsResponse(statisticResponse);

        return ResponseEntity.ok(statisticResponse);
    }

    public ResponseEntity<?> getProductStatistics (StatisticRequest statisticRequest)
    {
        StatisticResponse statisticResponse = new StatisticResponse();

        statisticRepository.getProductMap(statisticRequest, statisticResponse);

        new StatisticCalculator().populateStatisticsResponse(statisticResponse);

        return ResponseEntity.ok(statisticResponse);
    }

    public ResponseEntity<?> getStaffStatistics (StatisticRequest statisticRequest)
    {
        StatisticResponse statisticResponse = new StatisticResponse();

        statisticRepository.getStaffMap(statisticRequest, statisticResponse);

        new StatisticCalculator().populateStatisticsResponse(statisticResponse);

        return ResponseEntity.ok(statisticResponse);
    }
}

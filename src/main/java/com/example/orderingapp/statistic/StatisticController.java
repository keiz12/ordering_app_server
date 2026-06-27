package com.example.orderingapp.statistic;

import com.example.orderingapp.dto.statistic.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/secure/statistics")
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping
    public ResponseEntity<?> statistics (@RequestParam String startingDate, @RequestParam String endingDate, @RequestParam String statisticType)
    {
        StatisticRequest request = statisticType(startingDate, endingDate, statisticType);

        return switch (request.getStatisticType())
        {
            case StatisticType.REVENUE -> statisticService.getRevenueStatistics(request);

            case StatisticType.PRODUCT -> statisticService.getProductStatistics(request);

            case StatisticType.STAFF -> statisticService.getStaffStatistics(request);
        };
    }

    private StatisticRequest statisticType (String startingDate, String endingDate, String statisticType)
    {
        var request = new StatisticRequest();

        request.setStartingDate(startingDate);
        request.setEndingDate(endingDate);
        request.setStatisticType(StatisticType.valueOf(statisticType));

        return request;
    }
}

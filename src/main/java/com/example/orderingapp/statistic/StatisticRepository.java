package com.example.orderingapp.statistic;

import com.example.orderingapp.dto.statistic.*;
import lombok.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class StatisticRepository {

    private final JdbcTemplate jdbcTemplate;

    public void getRevenueMap (StatisticRequest request, StatisticResponse response)
    {
        String sql =
                """
                        SELECT DATE_FORMAT(o.created_at, '%d-%m-%Y'), SUM(op.product_quantity * p.price)
                        FROM `order` o
                        JOIN order_products op ON op.order_id = o.id
                        JOIN product p ON p.id = op.product_id
                        WHERE DATE(created_at) >= STR_TO_DATE(?, '%d-%m-%Y') AND DATE(created_at) <= STR_TO_DATE(?, '%d-%m-%Y')
                        GROUP BY (DATE_FORMAT(o.created_at, '%d-%m-%Y'))
                """;

        Map<String, BigDecimal> map = response.getRawDataMap();

        RowMapper<Void> mapper = (rs, rn) ->
        {
            map.put(rs.getString(1), rs.getBigDecimal(2));
            return null;
        };

        jdbcTemplate.query(sql, mapper, request.getStartingDate(), request.getEndingDate());
    }

    public void getProductMap (StatisticRequest request, StatisticResponse response)
    {
        String sql =
                """     
                SELECT p.name, SUM(op.product_quantity)
                FROM product p
                JOIN order_products op ON op.product_id = p.id
                JOIN `order` o ON o.id = op.order_id
                WHERE DATE(created_at) >= STR_TO_DATE(?, '%d-%m-%Y') AND DATE(created_at) <= STR_TO_DATE(?, '%d-%m-%Y')
                GROUP BY (p.id)
                """;

        Map<String, BigDecimal> map = response.getRawDataMap();

        RowMapper<Void> mapper = (rs, rn) ->
        {
            map.put(rs.getString(1), rs.getBigDecimal(2));
            return null;
        };

        jdbcTemplate.query(sql, mapper, request.getStartingDate(), request.getEndingDate());
    }

    public void getStaffMap (StatisticRequest request, StatisticResponse response)
    {
        String sql =
                """
                SELECT u.username, COUNT(spo.staff_id)
                FROM staff_processed_order spo
                JOIN user u ON u.id = spo.staff_id
                WHERE DATE(processed_at) >= STR_TO_DATE(?, '%d-%m-%Y') AND DATE(processed_at) <= STR_TO_DATE(?, '%d-%m-%Y')
                GROUP BY(spo.staff_id)
                """;

        Map<String, BigDecimal> map = response.getRawDataMap();

        RowMapper<Void> mapper = (rs, rn) ->
        {
            map.put(rs.getString(1), rs.getBigDecimal(2));
            return null;
        };

        jdbcTemplate.query(sql, mapper, request.getStartingDate(), request.getEndingDate());
    }
}

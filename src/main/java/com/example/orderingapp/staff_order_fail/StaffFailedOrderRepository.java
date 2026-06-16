package com.example.orderingapp.staff_order_fail;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StaffFailedOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    // CREATE
    public void insert(Long staffId, Long orderId, String cause) {
        String sql = """
                    INSERT INTO staff_failed_order (staff_id, order_id, cause)
                    VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(sql, staffId, orderId, cause);
    }

    // READ: all failed orders
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT staff_id, order_id, cause FROM staff_failed_order";
        return jdbcTemplate.queryForList(sql);
    }

    // READ: by orderId
    public List<Map<String, Object>> findByOrderId(Long orderId) {
        String sql = """
                    SELECT staff_id, order_id, cause
                    FROM staff_failed_order
                    WHERE order_id = ?
                """;

        return jdbcTemplate.queryForList(sql, orderId);
    }

    // DELETE: remove failure record
    public void delete(Long staffId, Long orderId) {
        String sql = """
                    DELETE FROM staff_failed_order
                    WHERE staff_id = ? AND order_id = ?
                """;

        jdbcTemplate.update(sql, staffId, orderId);
    }
}

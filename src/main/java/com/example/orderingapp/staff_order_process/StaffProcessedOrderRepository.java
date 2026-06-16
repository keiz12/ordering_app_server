package com.example.orderingapp.staff_order_process;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StaffProcessedOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(Long staffId, Long orderId) {
        String sql = "INSERT INTO staff_processed_order (staff_id, order_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, staffId, orderId);
    }

    public boolean delete(Long orderId) {
        String sql = "DELETE FROM staff_processed_order WHERE order_id = ?";
        return jdbcTemplate.update(sql, orderId) > 0;
    }

    public List<Map<String, Object>> findAllByDate(LocalDate date) {
        String sql = "SELECT staff_id, order_id FROM staff_processed_order " +
                "WHERE DATE(processed_at) = ?";
        return jdbcTemplate.queryForList(sql, date);
    }

    public Long findAssignedStaff(Long orderId) {
        String sql = "SELECT staff_id FROM staff_processed_order WHERE order_id=?";
        RowMapper<Long> mapper = (rs, rn) -> rs.getLong(1);
        List<Long> l = jdbcTemplate.query(sql, mapper, orderId);
        return l.isEmpty() ? null : l.getFirst();
    }

    public boolean orderPaymentConfirm (Long orderId) {
        String sql = "UPDATE `order` SET order_paid=1 WHERE id=?";
        return jdbcTemplate.update(sql, orderId) > 0;
    }

    public boolean orderPaymentUnConfirm (Long orderId) {
        String sql = "UPDATE `order` SET order_paid=0 WHERE id=?";
        return jdbcTemplate.update(sql, orderId) > 0;
    }

    public boolean isOrderAssigned(Long orderId)
    {
        String sql = "SELECT COUNT(*) FROM staff_processed_order WHERE order_id = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, orderId);

        return count != null && count > 0;
    }
}
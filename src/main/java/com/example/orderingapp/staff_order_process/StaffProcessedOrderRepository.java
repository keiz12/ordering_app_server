package com.example.orderingapp.staff_order_process;

import com.example.orderingapp.dto.order.*;
import com.example.orderingapp.dto.user.*;
import com.example.orderingapp.order.*;
import com.example.orderingapp.product.*;
import com.example.orderingapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.*;

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

    public List<OrderProcessIndicator> findAllByDate(String date) {

        String sql =
                """
                    SELECT o.uuid, s.processed_at
                    FROM `order` o 
                    LEFT JOIN staff_processed_order s ON s.order_id = o.id
                    WHERE DATE(created_at) = STR_TO_DATE(?, '%d-%m-%Y')
                   
                """;

        return jdbcTemplate.query(sql,
                (rs,rn) ->
                {
                    String uuid = rs.getString(1);
                    boolean isProcessed = !Objects.equals(rs.getObject(2, LocalDateTime.class), null);

                    var o = new OrderProcessIndicator();
                    o.setUuid(uuid);
                    o.setProcessed(isProcessed);

                    return o;
                },
                date);
    }

    public Long findAssignedStaff(Long orderId) {
        String sql = "SELECT staff_id FROM staff_processed_order WHERE order_id=?";
        RowMapper<Long> mapper = (rs, rn) -> rs.getLong(1);
        List<Long> l = jdbcTemplate.query(sql, mapper, orderId);
        return l.isEmpty() ? null : l.getFirst();
    }

    public List<LocalDateTime> getAllOrderCreatedAtDates () {
        String sql = "SELECT created_at FROM `order` order by created_at";
        return jdbcTemplate.query(sql, (rs,rn)-> rs.getObject(1, LocalDateTime.class));
    }

    public LocalDateTime findOrderProcessedTime (Long orderId)
    {
        String sql = "SELECT processed_at FROM staff_processed_order WHERE order_id=?";

        RowMapper<LocalDateTime> mapper = (rs, rn) -> rs.getObject(1, LocalDateTime.class);

        List<LocalDateTime> l = jdbcTemplate.query(sql, mapper, orderId);

        return l.isEmpty() ? null : l.getFirst();
    }

    public boolean orderPaymentConfirm (Long orderId) {
        String sql = "UPDATE `order` SET order_paid=1 WHERE id=?";
        return jdbcTemplate.update(sql, orderId) > 0;
    }

    public boolean orderPaymentRollback(Long orderId) {
        String sql = "UPDATE `order` SET order_paid=0 WHERE id=?";
        return jdbcTemplate.update(sql, orderId) > 0;
    }

    public boolean isOrderAssigned(Long orderId)
    {
        String sql = "SELECT COUNT(*) FROM staff_processed_order WHERE order_id = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, orderId);

        return count != null && count > 0;
    }

    public StaffProcessedOrder findStaffProcessedOrder (ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository, String orderUuid)
    {
        StaffProcessedOrder staffProcessedOrder = new StaffProcessedOrder();

        Order order = orderRepository.findOrderByUUID(orderUuid, productRepository);

        staffProcessedOrder.setOrder(order);

        Long assignedStaffID = findAssignedStaff(order.getId());

        LocalDateTime processedAt = findOrderProcessedTime(order.getId());

        Optional<UserDTO> optional = Optional.ofNullable(userRepository.findByUserID(assignedStaffID));

        optional.ifPresentOrElse((e) -> {
            staffProcessedOrder.setProcessedBy(e.getUsername());
        }, () -> {});

        staffProcessedOrder.setProcessedAt(processedAt);

        return staffProcessedOrder;
    }


}
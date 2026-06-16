package com.example.orderingapp.staff_order_process;

import com.example.orderingapp.dto.staff_order.StaffOrderDTO;
import com.example.orderingapp.order.OrderRepository;

import com.example.orderingapp.user.UserRepository;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffProcessedOrderService {

    private final StaffProcessedOrderRepository staffOrderRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate; // For WebSocket


    public void processOrder(String orderUuid)
    {
        Long staffId = getStaffID();

        Long orderId = orderRepository.findOrderIDByUUID(orderUuid);
        staffOrderRepository.save(staffId, orderId);

        // Trigger WebSocket notification
//        messagingTemplate.convertAndSend("/topic/orders",
//                Optional.of(Map.of("action", "processed", "staffId", staffId, "orderId", orderId)));
    }

    public void unprocessOrder(String orderUuid) {

        Long orderId = orderRepository.findOrderIDByUUID(orderUuid);

        staffOrderRepository.delete(orderId);

        // Trigger WebSocket notification
//        messagingTemplate.convertAndSend("/topic/orders",
//                Optional.of(Map.of("action", "unprocessed", "staffId", staffId, "orderId", orderId)));
    }

    public ResponseEntity<?> getProcessedOrdersByDate(LocalDate date)
    {
        StaffOrderDTO staffOrderDTO = new StaffOrderDTO();
        List<Map<String, Object>> mapList = staffOrderRepository.findAllByDate(date);
        Map<String, Object> map = mapList.getFirst();

        Long staff_id = (Long) map.get("staff_id");
        Long order_id = (Long) map.get("order_id");

        staffOrderDTO.setOrderResponseDTO(orderRepository.findOrdersByOrderID(order_id));
        staffOrderDTO.setUserDTO(userRepository.findByUserID(staff_id, true).get());

        return ResponseEntity.ok(staffOrderDTO);
    }

    public ResponseEntity<?> orderPaymentConfirm (String uuid) {

        Long orderId = orderRepository.findOrderIDByUUID(uuid);

        Long staff = staffOrderRepository.findAssignedStaff(orderId);

        if (getStaffID() == null || staff == null || !staff.equals(getStaffID())) {
            return ResponseEntity.badRequest().body("Payment confirmation fail\nYou are not assigned for this order");
        }
        staffOrderRepository.orderPaymentConfirm(orderRepository.findOrderIDByUUID(uuid));

        return ResponseEntity.badRequest().body("Payment confirmation succeed");
    }

    private Long getStaffID () {
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getUserIdByUsername(user.getUsername());
    }
}
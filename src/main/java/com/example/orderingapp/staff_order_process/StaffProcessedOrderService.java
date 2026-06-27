package com.example.orderingapp.staff_order_process;

import com.example.orderingapp.dto.order.*;
import com.example.orderingapp.order.OrderRepository;

import com.example.orderingapp.product.*;
import com.example.orderingapp.user.UserRepository;
import com.example.orderingapp.webSocket.*;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffProcessedOrderService {

    private final StaffProcessedOrderRepository staffOrderRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final WebSocketNotificationService notificationService; // For WebSocket


    public void processOrder(String orderUuid)
    {
        Long staffId = getStaffID();

        Long orderId = orderRepository.findOrderIDByUUID(orderUuid);
        staffOrderRepository.save(staffId, orderId);

        var staffProcessedOrder = staffOrderRepository.findStaffProcessedOrder(productRepository, orderRepository, userRepository, orderUuid);
        notificationService.staffProcessedOrder(staffProcessedOrder);
    }

    public void unprocessOrder(String orderUuid) {

        Long orderId = orderRepository.findOrderIDByUUID(orderUuid);
        staffOrderRepository.delete(orderId);
    }

    public ResponseEntity<?> getProcessedOrdersByDate(String date) {
        return ResponseEntity.ok(staffOrderRepository.findAllByDate(date));
    }

    public ResponseEntity<?> getAllOrderCreatedAtDates () {
        return ResponseEntity.ok(staffOrderRepository.getAllOrderCreatedAtDates());
    }

    public ResponseEntity<?> orderPaymentConfirm (String uuid) {

        Long orderId = orderRepository.findOrderIDByUUID(uuid);

        Long staff = staffOrderRepository.findAssignedStaff(orderId);

        if (getStaffID() == null || staff == null || !staff.equals(getStaffID()))
            return ResponseEntity.badRequest().body("Payment confirmation fail\nYou are not assigned for this order");

        staffOrderRepository.orderPaymentConfirm(orderRepository.findOrderIDByUUID(uuid));

        notificationService.staffConfirmOrderPayment(uuid);

        return ResponseEntity.ok("Payment confirmation succeed");
    }

    public ResponseEntity<?> orderPaymentRollback (String uuid) {

        Long orderId = orderRepository.findOrderIDByUUID(uuid);

        Long staff = staffOrderRepository.findAssignedStaff(orderId);

        if (getStaffID() == null || staff == null || !staff.equals(getStaffID()))
            return ResponseEntity.badRequest().body("Payment un-confirmation fail\nYou are not assigned for this order");

        staffOrderRepository.orderPaymentRollback(orderRepository.findOrderIDByUUID(uuid));

        notificationService.staffRollbackOrderPayment(uuid);

        return ResponseEntity.ok("Payment un-confirmation succeed");
    }

    private Long getStaffID () {
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getUserIdByUsername(user.getUsername());
    }

    public ResponseEntity<?> findStaffProcessedOrder (String orderUuid)
    {
        return ResponseEntity.ok(staffOrderRepository.findStaffProcessedOrder(productRepository, orderRepository, userRepository, orderUuid));
    }
}
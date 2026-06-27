package com.example.orderingapp.webSocket;

import com.example.orderingapp.dto.order.*;
import lombok.*;
import org.springframework.messaging.simp.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void clientMadeOrderOnEmployeeActivity(Order order) {
        messagingTemplate.convertAndSend("/client/made/order/employee/activity", order);
    }

    public void clientDeleteOrderOnEmployeeActivity(String uuid) {
        messagingTemplate.convertAndSend("/client/delete/order/employee/activity", uuid);
    }

    public void staffProcessedOrder(StaffProcessedOrder staffProcessedOrder) {
        messagingTemplate.convertAndSend("/client/staff/process/order", staffProcessedOrder);
    }

    public void staffConfirmOrderPayment (String uuid) {
        messagingTemplate.convertAndSend("/client/staff/confirm/order/payment", uuid);
    }

    public void staffRollbackOrderPayment (String uuid) {
        messagingTemplate.convertAndSend("/client/staff/rollback/order/payment", uuid);
    }
}

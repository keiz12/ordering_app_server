package com.example.orderingapp.staff_order_process;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StaffProcessedOrderController {

    private final StaffProcessedOrderService service;

    @PostMapping("/staff-orders/process/{orderUuid}")
    public ResponseEntity<?> processOrder (@PathVariable String orderUuid) {

        service.processOrder(orderUuid);
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping("/staff-orders/unprocess/{orderUuid}")
//    public ResponseEntity<Void> unprocessOrder (@PathVariable String orderUuid) {
//
//        service.unprocessOrder(orderUuid);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/secure/order/{uuid}")
    public ResponseEntity<?> getStaffProcessedOrder (@PathVariable String uuid) {
        return service.findStaffProcessedOrder(uuid);
    }

    @GetMapping("/secure/order/by-date/{date}")
    public ResponseEntity<?> getProcessedOrdersByDate(@PathVariable String date) {
        return service.getProcessedOrdersByDate(date);
    }

    @GetMapping("/secure/all/order/date")
    public ResponseEntity<?> getAllOrderCreatedAtDates() {
        return service.getAllOrderCreatedAtDates();
    }

    @PatchMapping ("/staff-orders/pay/confirm/{uuid}")
    public ResponseEntity<?> orderPaymentConfirm (@PathVariable String uuid) {
        return service.orderPaymentConfirm(uuid);
    }

    @PatchMapping ("/staff-orders/pay/rollback/{uuid}")
    public ResponseEntity<?> orderPaymentRollback (@PathVariable String uuid) {
        return service.orderPaymentRollback(uuid);
    }
}
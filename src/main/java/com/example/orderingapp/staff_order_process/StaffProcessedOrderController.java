package com.example.orderingapp.staff_order_process;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/staff-orders")
@RequiredArgsConstructor
public class StaffProcessedOrderController {

    private final StaffProcessedOrderService service;

    @PostMapping("/{orderUuid}")
    public ResponseEntity<Void> processOrder(
            @PathVariable String orderUuid) {

        service.processOrder(orderUuid);
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping("/{orderUuid}")
//    public ResponseEntity<Void> unprocessOrder(
//            @PathVariable String orderUuid) {
//
//        service.unprocessOrder(orderUuid);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/by-date")
    public ResponseEntity<?> getProcessedOrdersByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getProcessedOrdersByDate(date));
    }

    @PatchMapping ("/order-payment/{uuid}")
    public ResponseEntity<?> orderPaymentConfirm (@PathVariable String uuid) {
        return service.orderPaymentConfirm(uuid);
    }
}
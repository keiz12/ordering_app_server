package com.example.orderingapp.order;

import com.example.orderingapp.dto.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/all/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("@preAuthorizedService.isValidApiKey(#request.apiKey)")
    public ResponseEntity<?> createOrder(
            @RequestBody Order request)
    {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderUuid}")
    public ResponseEntity<?> isOrderPaid (@PathVariable String orderUuid) {
        return orderService.isOrderPaid(orderUuid);
    }

    @GetMapping("/order/{orderUuid}")
    public ResponseEntity<?> order (@PathVariable String orderUuid) {
        return orderService.getOrder(orderUuid);
    }

//    @PutMapping
//    @PreAuthorize("@preAuthorizedService.isValidApiKey(#request.apiKey)")
//    public ResponseEntity<?> updateOrder(
//            @RequestBody CreateOrderRequest request) {
//
//        return orderService.updateOrder(request);
//    }

    @DeleteMapping
    @PreAuthorize("@preAuthorizedService.isValidApiKey(#order.apiKey)")
    public ResponseEntity<?> deleteOrder(
            @RequestBody Order order)
    {
        return orderService.deleteOrder(order.getUuid());
    }

//    @GetMapping("/by-date")
//    public ResponseEntity<List<OrderResponseDTO>> getOrdersByDate(
//            @RequestParam
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate date) {
//
//        return ResponseEntity.ok(
//                orderService.getOrdersByDate(date)
//        );
//    }
}

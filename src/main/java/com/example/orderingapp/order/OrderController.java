package com.example.orderingapp.order;

import com.example.orderingapp.dto.order.CreateOrderRequest;
import com.example.orderingapp.dto.order.OrderFail;
import com.example.orderingapp.dto.order.OrderResponseDTO;
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
            @RequestBody CreateOrderRequest request) {

        return orderService.createOrder(request);
    }

    @PutMapping
    @PreAuthorize("@preAuthorizedService.isValidApiKey(#request.apiKey)")
    public ResponseEntity<?> updateOrder(
            @RequestBody CreateOrderRequest request) {

        return orderService.updateOrder(request);
    }

    @DeleteMapping
    @PreAuthorize("@preAuthorizedService.isValidApiKey(#request.apiKey)")
    public ResponseEntity<?> deleteOrder(
            @RequestPart CreateOrderRequest request, @RequestPart OrderFail orderFail)
    {
        return orderService.deleteOrder(orderFail);
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

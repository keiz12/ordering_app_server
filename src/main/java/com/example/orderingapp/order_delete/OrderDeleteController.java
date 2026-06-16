package com.example.orderingapp.order_delete;

import com.example.orderingapp.dto.order.CreateOrderRequest;
import com.example.orderingapp.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/secure/boss")
@RequiredArgsConstructor
public class OrderDeleteController {

    private final OrderService orderService;

    @DeleteMapping("/order/delete")
    public ResponseEntity<?> deleteOrder(
            @RequestBody CreateOrderRequest order)
    {
        return orderService.deleteOrder(order.getUuid());
    }
}

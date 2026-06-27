package com.example.orderingapp.staff_order_fail;

import com.example.orderingapp.dto.order.OrderFail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//@RestController
//@RequestMapping("/staff-failed-orders")
//@RequiredArgsConstructor

public class StaffFailedOrderController {

//    private final StaffFailedOrderService service;
//    // CREATE
//    @PostMapping
//    public ResponseEntity<Void> failOrder(
//            @RequestBody OrderFail dto)
//    {
//
//        service.markOrderFailed(dto);
//        return ResponseEntity.ok().build();
//    }
//
//    // READ ALL
//    @GetMapping
//    public ResponseEntity<?> getAll() {
//        return ResponseEntity.ok(service.getAllFailures());
//    }
//
//    // READ BY ORDER UUID
//    @GetMapping("/{orderUUID}")
//    public ResponseEntity<List<Map<String, Object>>> getByOrder(@PathVariable String orderUUID) {
//        return ResponseEntity.ok(service.getFailuresByOrder(orderUUID));
//    }
//
//    // DELETE
//    @DeleteMapping
//    public ResponseEntity<Void> delete(
//            @RequestBody OrderFail dto) {
//
//        service.deleteFailure(dto);
//        return ResponseEntity.ok().build();
//    }
}

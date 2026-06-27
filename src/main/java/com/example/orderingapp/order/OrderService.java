package com.example.orderingapp.order;

import com.example.orderingapp.dto.order.*;
import com.example.orderingapp.product.ProductRepository;
import com.example.orderingapp.staff_order_process.StaffProcessedOrderRepository;
import com.example.orderingapp.webSocket.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final StaffProcessedOrderRepository staffProcessedOrderRepository;
//
//    private final StaffFailedOrderRepository staffFailedOrderRepository;

    private final ProductRepository productRepository;

    private final WebSocketNotificationService notificationService;

    public ResponseEntity<?> createOrder (Order request) {

        orderRepository.createOrder(request, productRepository);

        notificationService.clientMadeOrderOnEmployeeActivity(request);

        return ResponseEntity.ok("Order created successfully");
    }

    public ResponseEntity<?> isOrderPaid (String orderUuid) {
        return ResponseEntity.ok(orderRepository.isOrderPaid(orderUuid));
    }

    public ResponseEntity<?> getOrder (String uuid) {
        return ResponseEntity.ok(orderRepository.findOrderByUUID(uuid, productRepository));
    }

    @Transactional
    public ResponseEntity<?> updateOrder(CreateOrderRequest request) {

        Long orderId = orderRepository.findOrderIDByUUID(request.getUuid());

        if (orderId == null) {
            throw new RuntimeException(
                    "Order not found for UUID: " + request.getUuid());
        }

        if (orderRepository.isOrderPaid(orderId)) {
            return ResponseEntity.badRequest().body("Paid orders cannot be modified");
        }

        CreateOrderRequest currentOrder =
                orderRepository.getOrderRequestById(orderId);

        setProductID (request);

        boolean updated = false;

        if (hasOrderTableChanged(currentOrder.getTableNumber(), request.getTableNumber()))
        {
            orderRepository.updateOrderTableNumber(orderId, request);
            updated = true;
        }

        if (hasOrderProductChanged(currentOrder.getOrderedProducts(), request.getOrderedProducts()))
        {
            orderRepository.updateOrderProduct(orderId, request.getOrderedProducts());
            updated = true;
        }

        if (updated)
            return ResponseEntity.accepted().body("Order Updated Successfully");
        else
            return ResponseEntity.badRequest().body("You have nothing to updated");
    }

    private void setProductID (CreateOrderRequest request)
    {
        request
                .getOrderedProducts()
                .forEach(e -> e.getProductDTO().setId(productRepository.findProductIdByName(e.getProductDTO().getName())));
    }

    public CreateOrderRequest getCreateOrderRequestByUUD (String uuid) {
        Long orderId = orderRepository.findOrderIDByUUID(uuid);
        return orderRepository.getOrderRequestById(orderId);
    }

    private boolean hasOrderTableChanged
            (int currTableNumber, int newTableNumber)
    {
        return currTableNumber != newTableNumber;
    }

    private boolean hasOrderProductChanged(
            List<OrderedProducts> currentProducts,
            List<OrderedProducts> incomingProducts) {

        if (currentProducts.size() != incomingProducts.size())
            return true;


        Map<Long, Integer> currentMap = currentProducts.stream()
                .collect(Collectors.toMap(
                        op -> op.getProductDTO().getId(),
                        OrderedProducts::getProductQuantity
                ));

        Map<Long, Integer> incomingMap = incomingProducts.stream()
                .collect(Collectors.toMap(
                        op -> productRepository.findProductIdByName(op.getProductDTO().getName()),
                        OrderedProducts::getProductQuantity
                ));

        return !currentMap.equals(incomingMap);
    }

//    @Transactional
//    public ResponseEntity<?> deleteOrder(OrderFail orderFail) {
//
//        Long orderId = orderRepository.findOrderIDByUUID(orderFail.getOrderUUID());
//
//        if (orderRepository.isOrderPaid(orderId))
//            return ResponseEntity.badRequest().body("Order can't be deleted as it is already paid");


//        Long staffId = staffProcessedOrderRepository.findAssignedStaff(orderId);

//        if (staffProcessedOrderRepository.delete(orderId))
//            staffFailedOrderRepository.insert(staffId, orderId, orderFail.getCause().toString());
//        else

//        orderRepository.deleteOrder(orderId);
//
//        return ResponseEntity.ok().body("Order deleted successfully");
//    }

    @Transactional
    public ResponseEntity<?> deleteOrder(String uuid) {

        Long orderId = orderRepository.findOrderIDByUUID(uuid);

        if (orderRepository.isOrderPaid(orderId))
            return ResponseEntity.badRequest().body("The order can't be deleted as it's already paid.");

        orderRepository.deleteOrder(orderId);

        notificationService.clientDeleteOrderOnEmployeeActivity(uuid);

        return ResponseEntity.ok().body("Order deleted successfully");
    }

//    public List<OrderResponseDTO> getOrdersByDate(LocalDate date) {
//        return orderRepository.findOrdersByDate(date);
//    }
}

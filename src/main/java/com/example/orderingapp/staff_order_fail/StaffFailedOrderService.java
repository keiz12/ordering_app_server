package com.example.orderingapp.staff_order_fail;

import com.example.orderingapp.dto.order.OrderFail;
import com.example.orderingapp.dto.staff_order.StaffOrderDTO;
import com.example.orderingapp.order.OrderRepository;
import com.example.orderingapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StaffFailedOrderService {

    private final StaffFailedOrderRepository repository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    public ResponseEntity<?> markOrderFailed(OrderFail dto)
    {
        Long staffId = userRepository.getUserIdByUsername(dto.getStaffUsername());
        Long orderId = orderRepository.findOrderIDByUUID(dto.getOrderUUID());

        if (staffId == null)
            return ResponseEntity.badRequest().body("No staff assigned to this order");

        if (orderId == null)
            return ResponseEntity.badRequest().body("The order doesn't exist");

        if (orderRepository.isOrderPaid(orderId))
            return ResponseEntity.badRequest().body("This order is already paid\nWe can't mark it failed");

        repository.insert(staffId, orderId, dto.getCause().toString());

        return ResponseEntity.ok("The order marked as failed");
    }

    public ResponseEntity<?> getAllFailures() {

        List<StaffOrderDTO> staffOrderDTOList = new ArrayList<>();

        List<Map<String, Object>> mapList = repository.findAll();

        mapList
                .forEach(e ->
                {
                    Long staff_id = (Long) e.get("staff_id");
                    Long order_id = (Long)e.get("order_id");

                    StaffOrderDTO staffOrderDTO = new StaffOrderDTO();
                    staffOrderDTO.setOrderResponseDTO(orderRepository.findOrdersByOrderID(order_id));
                    staffOrderDTO.setUserDTO(userRepository.findByUserID(staff_id, true).get());

                    staffOrderDTOList.add(staffOrderDTO);
                });

        return ResponseEntity.ok(mapList);
    }

    public List<Map<String, Object>> getFailuresByOrder(String orderUUID) {
        Long orderId = orderRepository.findOrderIDByUUID(orderUUID);
        return repository.findByOrderId(orderId);
    }

    public void deleteFailure(OrderFail dto) {
        Long staffId = userRepository.getUserIdByUsername(dto.getStaffUsername());
        Long orderId = orderRepository.findOrderIDByUUID(dto.getOrderUUID());
        repository.delete(staffId, orderId);
    }
}
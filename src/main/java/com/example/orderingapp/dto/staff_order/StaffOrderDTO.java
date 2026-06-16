package com.example.orderingapp.dto.staff_order;

import com.example.orderingapp.dto.order.OrderResponseDTO;
import com.example.orderingapp.dto.user.UserDTO;
import lombok.Data;

import java.util.List;

@Data
public class StaffOrderDTO {
    private UserDTO userDTO;
    private List<OrderResponseDTO> orderResponseDTO;
}

package com.example.orderingapp.dto.order;

import com.example.orderingapp.Enum.OrderFailCause;
import lombok.Data;

@Data
public class OrderFail {

    private OrderFailCause cause;

    private String orderUUID;

    private String staffUsername;
}

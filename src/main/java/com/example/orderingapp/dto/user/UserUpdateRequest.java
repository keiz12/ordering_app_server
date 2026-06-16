package com.example.orderingapp.dto.user;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private UserDTO oldUser;
    private UserDTO newUser;
}

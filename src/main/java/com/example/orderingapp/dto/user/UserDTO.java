package com.example.orderingapp.dto.user;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private String role;

}

package com.example.orderingapp.user;

import com.example.orderingapp.dto.user.UserDTO;
import com.example.orderingapp.dto.user.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/secure/boss/create/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {

        UserDTO savedUser = userService.createUser(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedUser);
    }

    @GetMapping("/secure/boss/all/users")
    public ResponseEntity<List<UserDTO>> getUsers () {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/secure/user")
    public ResponseEntity<UserDTO> getUser() {
        return ResponseEntity.ok().body(userService.getUser());
    }

    @PutMapping("/secure/boss/user")
    public ResponseEntity<String> updateUser(
            @RequestBody UserUpdateRequest request)
    {
        request.setOldUser(userService.getUser(request.getOldUser().getId()));
        userService.updateUser(
                request.getOldUser(),
                request.getNewUser()
        );

        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/secure/boss/user")
    public ResponseEntity<String> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok("User deleted successfully");
    }

    @DeleteMapping("/secure/boss/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}

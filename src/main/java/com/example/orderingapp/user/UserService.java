package com.example.orderingapp.user;

import com.example.orderingapp.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public UserDTO createUser(UserDTO userDTO) {

        Long userId = userRepository.saveUser(userDTO);

        userRepository.saveRole(userId,userDTO.getRole());

        userDTO.setId(userId);

        return userDTO;
    }


    public UserDTO getUser() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(user.getUsername()).orElseThrow();
    }

    public UserDTO getUser (Long id) {
        return userRepository.findByUserID(id).orElseThrow();
    }

    public List<UserDTO> getAllUsers () {
        return userRepository.getAllUsers();
    }

    public void updateUser(UserDTO oldUser, UserDTO newUser) {

        Long userId = userRepository.getUserIdByUsername(oldUser.getUsername());

        if (!Objects.equals(
                oldUser.getUsername(),
                newUser.getUsername())) {

            userRepository.updateUsername(
                    userId,
                    newUser.getUsername()
            );
        }

        if (!newUser.getPassword().isBlank() && !encoder.matches(newUser.getPassword(),oldUser.getPassword()))
        {
            userRepository.updatePassword(
                    userId,
                    newUser.getPassword()
            );
        }

        if (!Objects.equals(
                oldUser.getRole(),
                newUser.getRole())) {

            userRepository.updateRole(
                    userId,
                    newUser.getRole()
            );
        }
    }

    public void deleteUser()
    {
        var ctx = SecurityContextHolder.getContext();
        User user  = (User) ctx.getAuthentication().getPrincipal();

        String username = user.getUsername();
        Long userId = userRepository.getUserIdByUsername(username);

        if (userId == null) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteUserRoles(userId);
        userRepository.deleteUser(userId);
    }

    public void deleteUser(Long id)
    {
        userRepository.deleteUserRoles(id);
        userRepository.deleteUser(id);
    }
}

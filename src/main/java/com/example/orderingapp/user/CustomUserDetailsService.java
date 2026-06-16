package com.example.orderingapp.user;

import com.example.orderingapp.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    /**
     * Loads the user from the database by username and maps it into
     * a Spring Security UserDetails object with the user's role as a GrantedAuthority.
     *
     * @param username the username provided during authentication
     * @return a fully populated UserDetails object
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));

        return new User(
                user.getUsername(),
               user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

}

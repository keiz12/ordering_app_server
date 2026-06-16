package com.example.orderingapp.user.repository;

import com.example.orderingapp.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Fetches a user along with their role by joining the user
     * and authorization_roles tables on user_id.
     *
     * @param username the username to look up
     * @return an Optional containing the UserDTO if found, empty otherwise
     */
    public Optional<UserDTO> findByUsername(String username) {
        String sql = """
                SELECT u.id, u.username, u.password, ar.role
                FROM user u
                JOIN authorization_roles ar ON ar.user_id = u.id
                WHERE u.username = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UserDTO user = new UserDTO();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            return user;
        }, username).stream().findFirst();
    }

}

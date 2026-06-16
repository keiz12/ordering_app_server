package com.example.orderingapp.user;

import com.example.orderingapp.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;


    public Long saveUser(UserDTO userDTO) {

        String sql = """
                INSERT INTO user (username, password)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql,userDTO.getUsername(), passwordEncoder.encode(userDTO.getPassword()));

        return getUserIdByUsername(userDTO.getUsername());
    }

    public Long getUserIdByUsername(String username) {

        String sql = """
            SELECT id
            FROM user
            WHERE username = ?
            """;

        RowMapper<Long> rowMapper = (rs, rn) -> rs.getLong(1);

        List<Long> l = jdbcTemplate.query(sql, rowMapper, username);

        return l.isEmpty() ? null : l.getFirst();
    }

    public void saveRole(Long userId, String role) {

        String sql = """
                INSERT INTO authorization_roles (user_id, role)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql,userId,role);
    }

    public void updateUsername(Long userId, String username) {

        String sql = """
                UPDATE user
                SET username = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, username, userId);
    }

    public void updatePassword(Long userId, String password) {

        String sql = """
                UPDATE user
                SET password = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, passwordEncoder.encode(password), userId);
    }

    public void updateRole(Long userId, String role) {

        String sql = """
                UPDATE authorization_roles
                SET role = ?
                WHERE user_id = ?
                """;

        jdbcTemplate.update(sql, role, userId);
    }

    public void deleteUserRoles(Long userId) {

        String sql = """
                DELETE FROM authorization_roles
                WHERE user_id = ?
                """;

        jdbcTemplate.update(sql, userId);
    }

    public void deleteUser(Long userID) {

        String sql = """
                DELETE FROM user
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, userID);
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

    public Optional<UserDTO> findByUserID(Long user_id, boolean...noPassword) {
        String sql = """
                SELECT u.id, u.username, u.password, ar.role
                FROM user u
                JOIN authorization_roles ar ON ar.user_id = u.id
                WHERE u.id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UserDTO user = new UserDTO();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));

            if (noPassword.length == 0)
                user.setPassword(rs.getString("password"));

            user.setRole(rs.getString("role"));
            return user;
        }, user_id).stream().findFirst();
    }

    public List<UserDTO> getAllUsers ()
    {
        String sql =
                """
                        select user.id, user.username, authorization_roles.user_id, user.password from user
                        JOIN authorization_roles ON authorization_roles.user_ID = user.ID;
                """;

        List<UserDTO> userDTOList = new ArrayList<>();

        RowMapper <Void> mapper = (rs, rn) ->
        {
            var u = new UserDTO();

            u.setId(rs.getLong(1));
            u.setUsername(rs.getString(2));
            u.setRole(rs.getString(3));
            u.setPassword(rs.getString(4));
            userDTOList.add(u);

            return null;
        };

        jdbcTemplate.query(sql, mapper);

        return userDTOList;
    }
}

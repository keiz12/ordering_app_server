package com.example.orderingapp.androidKey;

import com.example.orderingapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AndroidKeyService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * Validates the raw key sent from the Android client by comparing it
     * against every encrypted key stored in the android_key table.
     * BCrypt does not support direct querying by raw value, so we fetch
     * all encrypted keys and use BCrypt's matches() to find a valid one.
     *
     * @return true if a matching key is found in the database, false otherwise
     */

    public void deleteAndroidKey () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sql = "DELETE FROM android_key WHERE created_by=?";
        jdbcTemplate.update(sql, userRepository.getUserIdByUsername(user.getUsername()));
    }

    public boolean isKeyValid(String rawKey) {
        String sql = "SELECT android_key FROM android_key";

        List<String> encryptedKeys = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("android_key")
        );

        return encryptedKeys.stream()
                .anyMatch(encryptedKey -> passwordEncoder.matches(rawKey, encryptedKey));
    }

    public void saveKey(String rawKey) {
        if (keyExists()) {
            throw new AndroidKeyAlreadyExistsException("An Android key already exists. It cannot be updated, only deleted.");
        }

        String encryptedKey = passwordEncoder.encode(rawKey);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String sql = "INSERT INTO android_key (android_key, created_by) VALUES (?,?)";
        jdbcTemplate.update(sql, encryptedKey, userRepository.getUserIdByUsername(user.getUsername()));
    }

    public boolean keyExists() {
        String sql = "SELECT COUNT(*) FROM android_key WHERE created_by=?";
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        RowMapper<Integer> mapper = (rs, rn) -> rs.getInt(1);

        Integer count = Optional.of( jdbcTemplate.queryForObject(sql, mapper, userRepository.getUserIdByUsername(user.getUsername())) ).orElse(null);
        return count != null && count > 0;
    }

    public String getAndroidKey() {
        String sql = "SELECT android_key FROM android_key LIMIT 1";
        return jdbcTemplate.queryForObject(sql, String.class);
    }
}

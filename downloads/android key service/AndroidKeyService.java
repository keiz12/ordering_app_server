package com.example.orderingapp.androidkey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AndroidKeyService {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AndroidKeyService(JdbcTemplate jdbcTemplate,
                             BCryptPasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Validates the raw key sent from the Android client by comparing it
     * against every encrypted key stored in the android_key table.
     * BCrypt does not support direct querying by raw value, so we fetch
     * all encrypted keys and use BCrypt's matches() to find a valid one.
     *
     * @param rawKey the plain-text key received from the Android client header
     * @return true if a matching key is found in the database, false otherwise
     */
    public boolean isKeyValid(String rawKey) {
        String sql = "SELECT encrypted_key FROM android_key";

        List<String> encryptedKeys = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("encrypted_key")
        );

        return encryptedKeys.stream()
                .anyMatch(encryptedKey -> passwordEncoder.matches(rawKey, encryptedKey));
    }

}

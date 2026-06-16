package com.example.orderingapp.androidkey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.orderingapp.androidkey.exception.AndroidKeyAlreadyExistsException;

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
        String sql = "SELECT android_key FROM android_key";

        List<String> encryptedKeys = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("android_key")
        );

        return encryptedKeys.stream()
                .anyMatch(encryptedKey -> passwordEncoder.matches(rawKey, encryptedKey));
    }

    /**
     * Checks if a key already exists in the android_key table.
     * The key is immutable and only one is allowed to exist at a time.
     *
     * @return true if a key already exists, false otherwise
     */
    public boolean keyExists() {
        String sql = "SELECT COUNT(*) FROM android_key";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null && count > 0;
    }

    /**
     * Saves the raw key into the android_key table after encrypting it with BCrypt.
     * Throws an exception if a key already exists since the key is immutable
     * and only one is allowed.
     *
     * @param rawKey the plain-text key provided by the BOSS user
     */
    public void saveKey(String rawKey) {
        if (keyExists()) {
            throw new AndroidKeyAlreadyExistsException("An Android key already exists. It cannot be updated, only deleted.");
        }

        String encryptedKey = passwordEncoder.encode(rawKey);
        String sql = "INSERT INTO android_key (android_key) VALUES (?)";
        jdbcTemplate.update(sql, encryptedKey);
    }

}

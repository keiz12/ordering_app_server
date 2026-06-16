package com.example.orderingapp.androidkey.controller;

import com.example.orderingapp.androidkey.exception.AndroidKeyAlreadyExistsException;
import com.example.orderingapp.androidkey.service.AndroidKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/android-key")
public class AndroidKeyController {

    private final AndroidKeyService androidKeyService;

    @Autowired
    public AndroidKeyController(AndroidKeyService androidKeyService) {
        this.androidKeyService = androidKeyService;
    }

    /**
     * Endpoint called on app launch to validate the key stored
     * in the Android Keystore against the server's android_key table.
     *
     * @param key the raw key passed as a request header
     * @return 200 OK if valid, 401 Unauthorized if not
     */
    @GetMapping("/validate")
    public ResponseEntity<String> validateKey(@RequestHeader("X-Android-Key") String key) {
        boolean isValid = androidKeyService.isKeyValid(key);

        if (isValid) {
            return ResponseEntity.ok("Key is valid.");
        } else {
            return ResponseEntity.status(401).body("Invalid key.");
        }
    }

    /**
     * Endpoint called by the BOSS to save the Android key to the database.
     * The key will be encrypted with BCrypt before being stored.
     * Only one key is allowed — throws 409 Conflict if one already exists.
     *
     * @param key the raw key provided by the BOSS in the request body
     * @return 201 Created if saved, 409 Conflict if a key already exists
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveKey(@RequestBody String key) {
        try {
            androidKeyService.saveKey(key);
            return ResponseEntity.status(201).body("Android key saved successfully.");
        } catch (AndroidKeyAlreadyExistsException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

}

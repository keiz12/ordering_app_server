package com.example.orderingapp.androidkey.controller;

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

}

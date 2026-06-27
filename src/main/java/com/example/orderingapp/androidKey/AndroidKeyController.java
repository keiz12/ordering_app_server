package com.example.orderingapp.androidKey;

import com.example.orderingapp.dto.key.AndroidKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secure/boss/android/key")
@RequiredArgsConstructor
public class AndroidKeyController {

    private final AndroidKeyService androidKeyService;

    /**
     * Endpoint called on app launch to validate the key stored
     * in the Android Keystore against the server's android_key table.
     *
     * @param androidKey the key passed as a request body
     * @return 200 OK if valid, 401 Unauthorized if not
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateKey(@RequestBody AndroidKey androidKey) {
        boolean isValid = androidKeyService.isKeyValid(androidKey.getKey());

        if (isValid) {
            return ResponseEntity.ok("Key is valid.");
        } else {
            return ResponseEntity.ok().body("Invalid key.");
        }
    }

    @PostMapping
    public ResponseEntity<?> saveKey(@RequestBody AndroidKey key) {
        try {
            androidKeyService.saveKey(key.getKey());
            return ResponseEntity.status(201).body("Android key saved successfully.");
        } catch (AndroidKeyAlreadyExistsException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }

    }

    @GetMapping("/count")
    public ResponseEntity<?> getKey() {
        if (!androidKeyService.keyExists()) {
            return ResponseEntity.ok().body("No Android key has been created yet.");
        }
        return ResponseEntity.ok(androidKeyService.getAndroidKeyCount() > 0 ? "API Key Exists" : "API key doesn't exist");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteKey () {
        androidKeyService.deleteAndroidKey();
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test");
    }
}

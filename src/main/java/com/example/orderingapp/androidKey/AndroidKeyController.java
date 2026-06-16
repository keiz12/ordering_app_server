package com.example.orderingapp.androidKey;

import com.example.orderingapp.dto.key.AndroidKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/validate")
    public ResponseEntity<String> validateKey(@RequestBody AndroidKey androidKey) {
        boolean isValid = androidKeyService.isKeyValid(androidKey.getKey());

        if (isValid) {
            return ResponseEntity.ok("Key is valid.");
        } else {
            return ResponseEntity.status(401).body("Invalid key.");
        }
    }

    @PostMapping
    public ResponseEntity<String> saveKey(@RequestBody AndroidKey key) {
        try {
            androidKeyService.saveKey(key.getKey());
            return ResponseEntity.status(201).body("Android key saved successfully.");
        } catch (AndroidKeyAlreadyExistsException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }

    }

    @GetMapping
    public ResponseEntity<String> getKey() {
        if (!androidKeyService.keyExists()) {
            return ResponseEntity.status(404).body("No Android key has been created yet.");
        }
        return ResponseEntity.ok(androidKeyService.getAndroidKey());
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

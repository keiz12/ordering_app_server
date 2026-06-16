package com.example.orderingapp.authorize;

import com.example.orderingapp.androidKey.AndroidKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("preAuthorizedService")
@RequiredArgsConstructor
public class PreAuthorizedService {

    private final AndroidKeyService androidKeyService;

    public boolean isValidApiKey(String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            return false;
        }

        return androidKeyService.isKeyValid(apiKey);
    }
}

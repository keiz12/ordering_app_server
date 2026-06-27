package com.example.orderingapp.test;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/all/get/test")
    public String allTest () {
        return "Test";
    }

    @GetMapping("/secure/get/test")
    public String secureTest () {
        return "Test";
    }
}

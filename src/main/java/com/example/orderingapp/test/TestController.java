package com.example.orderingapp.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

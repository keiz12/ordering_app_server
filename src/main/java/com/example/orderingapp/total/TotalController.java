package com.example.orderingapp.total;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TotalController
{
    private final TotalService totalService;

    @GetMapping("/secure/statistic/total")
    public ResponseEntity<?> totals () {
        return totalService.total();
    }
}

package com.example.orderingapp.total;

import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;

import java.util.*;

@RequiredArgsConstructor
@Service
public class TotalService {

    private final TotalRepository totalRepository;


    public ResponseEntity<?> total () {
        return ResponseEntity.ok(List.of(totalRepository.getTotalOrderPrice(), totalRepository.getTotalOrderedProducts(), totalRepository.getTotalStaff()));
    }
}

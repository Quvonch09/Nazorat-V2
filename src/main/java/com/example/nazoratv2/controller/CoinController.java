package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coin")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    @PutMapping("/minus/{studentId}")
    public ResponseEntity<ApiResponse<String>> minusCoin(@PathVariable Long studentId,
                                                         @RequestParam int coin) {
        return ResponseEntity.ok(coinService.minusCoin(studentId, coin));
    }


    @PutMapping("/plus/{studentId}")
    public ResponseEntity<ApiResponse<String>> plusCoin(@PathVariable Long studentId,
                                                        @RequestParam int coin) {
        return ResponseEntity.ok(coinService.plusCoin(studentId, coin));
    }
}

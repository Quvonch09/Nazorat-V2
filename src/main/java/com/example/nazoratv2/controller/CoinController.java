package com.example.nazoratv2.controller;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqCoin;
import com.example.nazoratv2.dto.response.ResCoin;
import com.example.nazoratv2.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @PostMapping
    public ResponseEntity<ApiResponse<String>> saveCoin(@RequestParam ReqCoin reqCoin){
        return ResponseEntity.ok(coinService.saveCoin(reqCoin));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateCoin(@RequestParam ResCoin reqCoin){
        return ResponseEntity.ok(coinService.updateCoin(reqCoin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCoin(@PathVariable Long id){
        return ResponseEntity.ok(coinService.deleteCoin(id));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<ResCoin>>> getAllCoin(){
        return ResponseEntity.ok(coinService.getAllCoin());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResCoin>> getCoinById(@PathVariable Long id){
        return ResponseEntity.ok(coinService.getCoinById(id));
    }
}

package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqCoin;
import com.example.nazoratv2.dto.response.ResCoin;
import com.example.nazoratv2.entity.Coin;
import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.CoinMapper;
import com.example.nazoratv2.repository.CoinRepository;
import com.example.nazoratv2.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinService {

    private final StudentRepository studentRepository;
    private final CoinRepository coinRepository;
    private final CoinMapper coinMapper;

    public ApiResponse<String> minusCoin(Long studentId, int coin){
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );

        student.setCoin(student.getCoin() - coin);
        studentRepository.save(student);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> plusCoin(Long studentId, int coin){
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );

        student.setCoin(student.getCoin() + coin);
        studentRepository.save(student);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> saveCoin(ReqCoin reqCoin){
        Coin coin = Coin.builder()
                .name(reqCoin.getName())
                .description(reqCoin.getDescription())
                .imgUrl(reqCoin.getImgUrl())
                .coin(reqCoin.getCoin())
                .build();
        coinRepository.save(coin);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> updateCoin(ResCoin resCoin){
        Coin coin = coinRepository.findById(resCoin.getId()).orElseThrow(
                () -> new DataNotFoundException("Coin not found")
        );

        coin.setName(resCoin.getName());
        coin.setDescription(resCoin.getDescription());
        coin.setImgUrl(resCoin.getImgUrl());
        coin.setCoin(resCoin.getCoin());
        coinRepository.save(coin);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<String> deleteCoin(Long id){
        Coin coin = coinRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Coin not found")
        );
        coinRepository.delete(coin);
        return ApiResponse.success(null, "Success");
    }


    public ApiResponse<ResCoin> getCoinById(Long id) {
        Coin coin = coinRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Coin not found")
        );
        return ApiResponse.success(coinMapper.resCoin(coin), "Success");
    }


    public ApiResponse<List<ResCoin>> getAllCoin(){
        List<ResCoin> list = coinRepository.findAll().stream().map(coinMapper::resCoin).toList();
        return ApiResponse.success(list, "Success");
    }
}

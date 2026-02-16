package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.response.ResCoin;
import com.example.nazoratv2.entity.Coin;
import org.springframework.stereotype.Component;

@Component
public class CoinMapper {
    public ResCoin resCoin(Coin coin) {
        return ResCoin.builder()
                .id(coin.getId())
                .name(coin.getName())
                .description(coin.getDescription())
                .imgUrl(coin.getImgUrl())
                .coin(coin.getCoin())
                .build();
    }
}

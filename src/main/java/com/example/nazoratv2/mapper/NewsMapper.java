package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.request.ReqNews;
import com.example.nazoratv2.dto.response.ResNews;
import com.example.nazoratv2.entity.News;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class NewsMapper {

    public News toEntity(ReqNews req) {
        return News.builder()
                .date(LocalDate.parse(req.getTitle()))
                .description(req.getDescription())
                .imgUrl(req.getImgUrl())
                .date(LocalDate.parse(req.getDate()))
                .build();
    }

    public ResNews toDto(News news) {
        ResNews res = new ResNews();
        res.setId(news.getId());
        res.setTitle(news.getTitle());
        res.setDescription(news.getDescription());
        res.setImgUrl(news.getImgUrl());
        res.setDate(String.valueOf(news.getDate()));
        res.setCategoryName(news.getCategory().getName());
        res.setMarkName(news.getMark().getName());
        res.setGroupName(news.getGroup().getName());
        return res;
    }

}

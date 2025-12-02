package com.example.nazoratv2.mapper;

import com.example.nazoratv2.dto.request.ReqCategory;
import com.example.nazoratv2.dto.response.ResCategory;
import com.example.nazoratv2.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public ReqCategory toReq(Category category) {
        if (category == null) return null;

        return ReqCategory.builder()
                .imgUrl(String.valueOf(category.getId()))
                .name(category.getName())
                .imgUrl(category.getImgUrl())
                .build();
    }

    public ResCategory toRes(Category category) {
        return ResCategory.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imgUrl(category.getImgUrl())
                .duration(category.getDuration())
                .build();
    }


//    public Category toEntity(ReqCategory req) {
//        if (req == null) return null;
//
//        Category category = new Category();
//        category.setId(req.getId());
//        category.setName(req.getName());
//        category.setImgUrl(req.getImgUrl());
//        return category;
//    }


    public void update(Category category, ReqCategory req) {
        if (req.getName() != null)
            category.setName(req.getName());

        if (req.getImgUrl() != null)
            category.setImgUrl(req.getImgUrl());
    }

}

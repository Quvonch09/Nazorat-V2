package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqNews;
import com.example.nazoratv2.dto.response.ResNews;
import com.example.nazoratv2.entity.Category;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.Mark;
import com.example.nazoratv2.entity.News;
import com.example.nazoratv2.mapper.NewsMapper;
import com.example.nazoratv2.repository.CategoryRepository;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.MarkRepository;
import com.example.nazoratv2.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final MarkRepository markRepository;
    private final GroupRepository groupRepository;
    private final NewsMapper newsMapper;

    public ApiResponse<String> add(ReqNews req) {

        News news = newsMapper.toEntity(req);

        newsRepository.save(news);

        return ApiResponse.success(null, "Success");
    }

    public ApiResponse<List<ResNews>> getAll() {
        List<ResNews> list = newsRepository.findAll()
                .stream()
                .map(newsMapper::toDto)
                .toList();
        return ApiResponse.success(list, "Success");
    }

    public ApiResponse<ResNews> getOne(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        return ApiResponse.success(newsMapper.toDto(news), "Success");
    }

    public ApiResponse<String> delete(Long id) {
        newsRepository.deleteById(id);
        return ApiResponse.success(null,"Deleted");
    }

}

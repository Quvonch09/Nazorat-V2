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

    public ApiResponse add(ReqNews req) {

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Mark mark = markRepository.findById(req.getMarkId())
                .orElseThrow(() -> new RuntimeException("Mark not found"));

        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        News news = newsMapper.toEntity(req);
        news.setCategory(category);
        news.setMark(mark);
        news.setGroup(group);

        newsRepository.save(news);

        return ApiResponse.success("News added");
    }

    public ApiResponse getAll() {
        List<ResNews> list = newsRepository.findAll()
                .stream()
                .map(newsMapper::toDto)
                .toList();
        return ApiResponse.success(list);
    }

    public ApiResponse getOne(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        return ApiResponse.success(newsMapper.toDto(news));
    }

    public ApiResponse delete(Long id) {
        newsRepository.deleteById(id);
        return ApiResponse.success("Deleted");
    }

}

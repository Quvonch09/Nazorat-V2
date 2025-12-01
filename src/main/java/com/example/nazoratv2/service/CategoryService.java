package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.ApiResponse;
import com.example.nazoratv2.dto.request.ReqCategory;
import com.example.nazoratv2.dto.response.ResCategory;
import com.example.nazoratv2.entity.Category;
import com.example.nazoratv2.exception.DataNotFoundException;
import com.example.nazoratv2.mapper.GroupMapper;
import com.example.nazoratv2.repository.CategoryRepository;
import com.example.nazoratv2.repository.GroupRepository;
import com.example.nazoratv2.repository.RoomRepository;
import com.example.nazoratv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final GroupMapper groupMapper;
    private final CategoryRepository categoryRepository;

    public ApiResponse<String> saveCategory(ReqCategory reqCategory) {

        Category category = Category.builder()
                .name(reqCategory.getName())
                .description(reqCategory.getDescription())
                .build();

        categoryRepository.save(category);
        return ApiResponse.success(null, "Category successfully saved");
    }


    public ApiResponse<String> updateCategory(Long id, ReqCategory reqCategory) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        category.setName(reqCategory.getName());
        category.setDescription(reqCategory.getDescription());

        categoryRepository.save(category);

        return ApiResponse.success(null, "Category successfully updated");
    }


    public ApiResponse<String> deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        categoryRepository.delete(category);
        return ApiResponse.success(null, "Category successfully deleted");
    }





    public ApiResponse<ResCategory> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Category not found")
        );

        return ApiResponse.success(categoryMapper.toDto(category), "Success");
    }


    public ApiResponse<List<ResCategory>> getAllCategory() {
        List<Category> list = categoryRepository.findAll();

        if (list.isEmpty()) {
            return ApiResponse.error("Category not found");
        }

        Object categoryMapper;
        List<ResCategory> res = list.stream()
                .map(categoryMapper::toDto)
                .toList();

        return ApiResponse.success(res, "Success");
    }


}

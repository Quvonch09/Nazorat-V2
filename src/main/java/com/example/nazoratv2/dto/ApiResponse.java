package com.example.nazoratv2.dto;

import com.example.nazoratv2.dto.response.ResTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public ApiResponse(String taskNotFound, boolean b) {
    }

    public ApiResponse(String taskUpdatedSuccessfully, boolean b, ResTask dto) {
    }

    public static <T> ApiResponse<T> success(T data,String message) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}

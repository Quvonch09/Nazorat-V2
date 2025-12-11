package com.example.nazoratv2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {
    @Schema(hidden = true)
    private Long id;
    private String fullName;
    private String phone;
    private String imageUrl;
    @Schema(hidden = true)
    private String role;
}

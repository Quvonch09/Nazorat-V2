package com.example.nazoratv2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDTO {
    @Schema(hidden = true)
    private Long id;

    private String name;
}

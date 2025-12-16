package com.example.nazoratv2.dto;

import com.example.nazoratv2.entity.enums.AttendaceEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDto {

    @Schema(hidden = true)
    private Long id;

    @Schema(hidden = true)
    private String fullName;

    private Long studentId;

    private AttendaceEnum status;

    private String description;

    private LocalDate date;
}

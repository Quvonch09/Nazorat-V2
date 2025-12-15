package com.example.nazoratv2.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReqTask {

    private Long userId;

    private Long groupId;

    private String title;

    private String description;

    private LocalDate deadline;
}

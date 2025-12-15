package com.example.nazoratv2.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResTask {

    private Long id;
    private String title;
    private String description;
    private LocalDate deadline;
    private Long userId;
    private String userName;
    private Long groupId;
    private String groupName;

}

package com.example.nazoratv2.dto.dashboard;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherDashboard {
    private int studentCount;
    private int groupCount;
    private long countLesson;
}

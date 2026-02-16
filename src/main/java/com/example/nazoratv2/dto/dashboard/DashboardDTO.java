package com.example.nazoratv2.dto.dashboard;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDTO {
    private Long countStudents;
    private Long countRooms;
    private Long countGroups;
    private Long countCategory;
    private Long countEmployees;
    private Long countLessons;
}

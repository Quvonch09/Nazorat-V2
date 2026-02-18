package com.example.nazoratv2.dto;

import com.example.nazoratv2.entity.enums.WeekDays;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupScheduleDTO {
    private String groupName;
    private String teacherName;
    private String startTime;
    private String endTime;
    private List<WeekDays> weekDays;
}

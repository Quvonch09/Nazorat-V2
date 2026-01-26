package com.example.nazoratv2.dto;

import com.example.nazoratv2.entity.enums.WeekDays;

import java.util.List;


public record DayStat(
        WeekDays day,
        List<TimeInterval> busy,
        List<TimeInterval> free
) {}

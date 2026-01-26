package com.example.nazoratv2.service;

import com.example.nazoratv2.dto.DayStat;
import com.example.nazoratv2.dto.TimeInterval;
import com.example.nazoratv2.dto.request.ReqGroupDTO;
import com.example.nazoratv2.entity.Group;
import com.example.nazoratv2.entity.enums.WeekDays;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomScheduleStats {

    // Sizning markazingiz dars beradigan umumiy vaqt oralig'i (o'zingizga moslang)
    private static final LocalTime DAY_START = LocalTime.of(8, 0);
    private static final LocalTime DAY_END   = LocalTime.of(20, 0);

    public static List<DayStat> buildWeeklyStats(List<Group> schedules) {

        // 1) Kunlarga guruhlash
        Map<WeekDays, List<Group>> byDay = schedules.stream()
                .flatMap(g -> g.getWeekDays().stream()
                        .map(day -> Map.entry(day, g)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));// <-- DTO da dayOfWeek bo'lishi kerak!

        List<DayStat> result = new ArrayList<>();

        for (WeekDays day : WeekDays.values()) {
            List<Group> daySchedules = byDay.getOrDefault(day, List.of());

            // 2) Busy interval list
            List<TimeInterval> busy = daySchedules.stream()
                    .map(s -> new TimeInterval(s.getStartTime(), s.getEndTime()))
                    .sorted(Comparator.comparing(TimeInterval::start))
                    .toList();

            // 3) Merge (overlap / touching)
            List<TimeInterval> mergedBusy = merge(busy);

            // 4) Free interval list
            List<TimeInterval> free = invert(mergedBusy, DAY_START, DAY_END);

            result.add(new DayStat(day, mergedBusy, free));
        }

        return result;
    }

    private static List<TimeInterval> merge(List<TimeInterval> intervals) {
        if (intervals.isEmpty()) return List.of();

        List<TimeInterval> merged = new ArrayList<>();
        TimeInterval cur = intervals.get(0);

        for (int i = 1; i < intervals.size(); i++) {
            TimeInterval nxt = intervals.get(i);

            // overlap yoki ketma-ket (end == start) bo'lsa birlashtiramiz
            if (!nxt.start().isAfter(cur.end())) {
                LocalTime newEnd = cur.end().isAfter(nxt.end()) ? cur.end() : nxt.end();
                cur = new TimeInterval(cur.start(), newEnd);
            } else {
                merged.add(cur);
                cur = nxt;
            }
        }
        merged.add(cur);
        return merged;
    }

    private static List<TimeInterval> invert(List<TimeInterval> busy, LocalTime start, LocalTime end) {
        List<TimeInterval> free = new ArrayList<>();
        LocalTime cursor = start;

        for (TimeInterval b : busy) {
            if (b.start().isAfter(cursor)) {
                free.add(new TimeInterval(cursor, b.start()));
            }
            if (b.end().isAfter(cursor)) {
                cursor = b.end();
            }
        }

        if (cursor.isBefore(end)) {
            free.add(new TimeInterval(cursor, end));
        }

        return free;
    }
}

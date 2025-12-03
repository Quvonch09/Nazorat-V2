package com.example.nazoratv2.dto.request;

import com.example.nazoratv2.entity.enums.AttendaceEnum;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqAttendance {

    private LocalDate date;

    private Long studentId;

    private AttendaceEnum status;
}

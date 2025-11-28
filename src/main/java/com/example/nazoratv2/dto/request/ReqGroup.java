package com.example.nazoratv2.dto.request;


import com.example.nazoratv2.dto.StudentDTO;
import com.example.nazoratv2.dto.response.ResStudent;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqGroup {

    @Schema(hidden = true)
    private Long id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String endTime;

    private List<String> weekDays;

    private Long teacherId;

    private Long roomId;

    @Schema(hidden = true)
    private String roomName;

    @Schema(hidden = true)
    private String teacherName;

    @Schema(hidden = true)
    private List<ResStudent> students;
}

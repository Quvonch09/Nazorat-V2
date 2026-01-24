package com.example.nazoratv2.dto.response;

import com.example.nazoratv2.dto.request.ReqGroupDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResRoom {
    private Long id;

    private String name;

    private List<ReqGroupDTO> schedules;
}

package com.example.nazoratv2.dto.response;

import com.example.nazoratv2.dto.request.ReqGroupDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResTeacher {
    private Long id;
    private String fullName;
    private String phone;
    private String imageUrl;
    private List<ResStudent> studentList;
    private List<ReqGroupDTO> groupList;
}

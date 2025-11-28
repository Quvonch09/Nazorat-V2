package com.example.nazoratv2.dto.response;

import com.example.nazoratv2.entity.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResStudent {

    private Long id;
    private String fulName;
    private String imgUrl;
    private String phoneNumber;
    private Long groupId;
    private String groupName;

}

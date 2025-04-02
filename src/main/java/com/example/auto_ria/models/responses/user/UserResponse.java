package com.example.auto_ria.models.responses.user;

import java.time.LocalDateTime;
import java.util.List;

import com.example.auto_ria.enums.ERole;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private int id;
    private String name;
    private String lastName;
    private String city;
    private String country;
    private String number;
    private String avatar;
    private List<ERole> role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

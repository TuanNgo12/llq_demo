package com.example.demo.dto.request.groupCategory;

import lombok.Data;

import java.util.List;

@Data
public class StatusRequest {

    private List<Long> ids;
    private Integer status;
}

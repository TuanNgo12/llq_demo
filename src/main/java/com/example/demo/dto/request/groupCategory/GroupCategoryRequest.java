package com.example.demo.dto.request.groupCategory;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class GroupCategoryRequest {

    private Long id;

    @NotBlank(message = "Tên tham số không được để trống")
    private String paramName;

    @NotBlank(message = "Giá trị tham số không được để trống")
    private String paramValue;

    @NotBlank(message = "Loại tham số không được để trống")
    private String paramType;

    private String description;

    @NotBlank(message = "Cấu phần xử lý không được để trống")
    private String componentCode;

    private Integer status =1;

    private Integer isActive=0;

    private Integer isDisplay=1;

    @Size(max = 4000, message = "NEW_DATA không quá 4000 ký tự")
    private String newData;

    @NotNull(message = "Ngày hiệu lực không được để trống")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss",  timezone = "Asia/Ho_Chi_Minh")
    private Date effectiveDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss",  timezone = "Asia/Ho_Chi_Minh")
    private Date endEffectiveDate;

    private Date createdDate;

    private Date updatedDate;

}

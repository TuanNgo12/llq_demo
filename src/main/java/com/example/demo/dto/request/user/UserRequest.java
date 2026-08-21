package com.example.demo.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {

    private Long id;

    @NotBlank(message = "UserName không được để trống!")
    private String userName;

    @NotBlank(message = "Email không được để trống!")
    private String email;

    @NotBlank(message = "Password không được để trống!")
    private String passWord;

}

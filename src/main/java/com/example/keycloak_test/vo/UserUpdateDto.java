package com.example.keycloak_test.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserUpdateDto {

    @ApiModelProperty(example = "이메일")
    private String email;

    @ApiModelProperty(example = "패스워드")
    private String password;

    @ApiModelProperty(example = "새로운 패스워드")
    private String newPassword;

    @NotNull
    @ApiModelProperty(example = "유저 권한")
    private UserRole userRole;

    private int status;
    private String statusInfo;
}

package com.kindergarten.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 用户DTO
 */
@Data
public class UserDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "角色不能为空")
    private String role;
}

package com.kindergarten.vo;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginResponse {

    private String token;

    private Long userId;

    private String username;

    private String realName;

    private String role;

    private String avatarUrl;

    public LoginResponse(String token, Long userId, String username, String realName, String role, String avatarUrl) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }
}

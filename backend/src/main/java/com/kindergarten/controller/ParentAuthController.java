package com.kindergarten.controller;

import com.kindergarten.common.Result;
import com.kindergarten.dto.LoginRequest;
import com.kindergarten.dto.RegisterRequest;
import com.kindergarten.service.AuthService;
import com.kindergarten.vo.LoginResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 家长端认证控制器
 */
@Api(tags = "家长端-认证接口")
@RestController
@RequestMapping("/parent")
public class ParentAuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation("家长注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("家长登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("退出成功");
    }
}

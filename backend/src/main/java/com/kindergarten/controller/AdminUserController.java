package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.dto.UserDTO;
import com.kindergarten.entity.SysUser;
import com.kindergarten.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端-用户管理
 */
@Api(tags = "管理员端-用户管理")
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private AdminService adminService;

    @ApiOperation("获取用户列表")
    @GetMapping("")
    public Result<Page<SysUser>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String role) {
        Page<SysUser> page = adminService.getUserList(pageNum, pageSize, role);
        return Result.success(page);
    }

    @ApiOperation("新增用户")
    @PostMapping("")
    public Result<Void> addUser(@Validated @RequestBody UserDTO dto) {
        adminService.addUser(dto);
        return Result.success("用户创建成功");
    }

    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @Validated @RequestBody UserDTO dto) {
        adminService.updateUser(id, dto);
        return Result.success("用户更新成功");
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success("用户删除成功");
    }

    @ApiOperation("启用/禁用用户")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success("状态更新成功");
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        adminService.resetPassword(id);
        return Result.success("密码已重置为123456");
    }
}

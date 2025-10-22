package com.kindergarten.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.LeaveRequest;
import com.kindergarten.mapper.LeaveRequestMapper;
import com.kindergarten.service.LeaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端-请假审批
 */
@Api(tags = "管理员端-请假审批")
@RestController
@RequestMapping("/admin/leave")
public class AdminLeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @ApiOperation("获取请假申请列表")
    @GetMapping("")
    public Result<Page<LeaveRequest>> getLeaveList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {

        Page<LeaveRequest> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();

        if (status != null && !status.isEmpty()) {
            wrapper.eq(LeaveRequest::getStatus, status);
        }

        wrapper.orderByDesc(LeaveRequest::getCreatedTime);
        Page<LeaveRequest> result = leaveRequestMapper.selectPage(page, wrapper);

        return Result.success(result);
    }

    @ApiOperation("审批通过")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(
            @PathVariable Long id,
            @RequestParam(required = false) String remark,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        leaveService.approveLeaveRequest(id, userId, true, remark);
        return Result.success("审批通过");
    }

    @ApiOperation("审批拒绝")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(
            @PathVariable Long id,
            @RequestParam String remark,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        leaveService.approveLeaveRequest(id, userId, false, remark);
        return Result.success("已拒绝");
    }
}

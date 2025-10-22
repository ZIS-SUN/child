package com.kindergarten.controller;

import com.kindergarten.common.Result;
import com.kindergarten.dto.LeaveRequestDTO;
import com.kindergarten.entity.LeaveRequest;
import com.kindergarten.service.LeaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家长端-请假管理
 */
@Api(tags = "家长端-请假管理")
@RestController
@RequestMapping("/parent/leave")
public class ParentLeaveController {

    @Autowired
    private LeaveService leaveService;

    @ApiOperation("提交请假申请")
    @PostMapping("")
    public Result<Void> submitLeave(@Validated @RequestBody LeaveRequestDTO dto, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        leaveService.submitLeaveRequest(userId, dto);
        return Result.success("请假申请已提交");
    }

    @ApiOperation("获取请假记录列表")
    @GetMapping("")
    public Result<List<LeaveRequest>> getLeaveList(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<LeaveRequest> list = leaveService.getLeaveList(userId);
        return Result.success(list);
    }

    @ApiOperation("撤销请假申请")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelLeave(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        leaveService.cancelLeaveRequest(userId, id);
        return Result.success("请假申请已撤销");
    }
}

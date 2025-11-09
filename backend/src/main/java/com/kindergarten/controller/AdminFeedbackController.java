package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.Feedback;
import com.kindergarten.service.FeedbackService;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 管理员端-意见反馈管理
 */
@Api(tags = "管理员端-意见反馈管理")
@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("获取反馈列表")
    @GetMapping("")
    public Result<Page<Feedback>> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Page<Feedback> page = feedbackService.getFeedbackList(pageNum, pageSize, status);
        return Result.success(page);
    }

    @ApiOperation("获取反馈详情")
    @GetMapping("/{id}")
    public Result<Feedback> getFeedbackDetail(@PathVariable Long id) {
        Feedback feedback = feedbackService.getFeedbackById(id);
        return Result.success(feedback);
    }

    @ApiOperation("回复反馈")
    @PostMapping("/{id}/reply")
    public Result<Void> replyFeedback(
            @PathVariable Long id,
            @RequestParam String reply,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long replierId = jwtUtil.getUserIdFromToken(token);

        feedbackService.replyFeedback(id, replierId, reply);
        return Result.success("回复成功");
    }

    @ApiOperation("删除反馈")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return Result.success("删除成功");
    }
}

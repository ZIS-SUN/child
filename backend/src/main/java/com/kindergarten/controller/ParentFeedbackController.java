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
 * 家长端-意见反馈
 */
@Api(tags = "家长端-意见反馈")
@RestController
@RequestMapping("/parent/feedback")
public class ParentFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("获取我的反馈列表")
    @GetMapping("")
    public Result<Page<Feedback>> getMyFeedbackList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Page<Feedback> page = feedbackService.getUserFeedbackList(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("提交反馈")
    @PostMapping("")
    public Result<Void> submitFeedback(
            @RequestParam String content,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        feedbackService.submitFeedback(userId, content);
        return Result.success("提交成功");
    }

    @ApiOperation("删除反馈")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return Result.success("删除成功");
    }
}

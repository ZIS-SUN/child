package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.Interaction;
import com.kindergarten.service.InteractionService;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员/教师端-家园互动管理
 */
@Api(tags = "管理员端-家园互动管理")
@RestController
@RequestMapping("/admin/interaction")
public class AdminInteractionController {

    @Autowired
    private InteractionService interactionService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("获取互动列表")
    @GetMapping("")
    public Result<Page<Interaction>> getInteractionList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long childId) {
        Page<Interaction> page = interactionService.getInteractionList(
                pageNum, pageSize, parentId, teacherId, childId);
        return Result.success(page);
    }

    @ApiOperation("获取互动详情")
    @GetMapping("/{id}")
    public Result<Interaction> getInteractionDetail(@PathVariable Long id) {
        Interaction interaction = interactionService.getInteractionById(id);
        return Result.success(interaction);
    }

    @ApiOperation("回复互动")
    @PostMapping("/{id}/reply")
    public Result<Void> replyInteraction(
            @PathVariable Long id,
            @RequestParam String reply) {
        interactionService.replyInteraction(id, reply);
        return Result.success("回复成功");
    }

    @ApiOperation("标记为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        interactionService.markAsRead(id);
        return Result.success("标记成功");
    }

    @ApiOperation("删除互动")
    @DeleteMapping("/{id}")
    public Result<Void> deleteInteraction(@PathVariable Long id) {
        interactionService.deleteInteraction(id);
        return Result.success("删除成功");
    }

    @ApiOperation("获取未读互动数量")
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> getUnreadCount(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long teacherId = jwtUtil.getUserIdFromToken(token);

        Long count = interactionService.getUnreadCount(teacherId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }
}

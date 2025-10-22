package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.Notice;
import com.kindergarten.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 家长端-通知公告
 */
@Api(tags = "家长端-通知公告")
@RestController
@RequestMapping("/parent/notices")
public class ParentNoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("获取通知列表")
    @GetMapping("")
    public Result<Page<Notice>> getNoticeList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Notice> page = noticeService.getNoticeList(pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("获取通知详情")
    @GetMapping("/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeDetail(id);
        return Result.success(notice);
    }

    @ApiOperation("标记通知已读")
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        noticeService.markAsRead(id, userId);
        return Result.success();
    }
}

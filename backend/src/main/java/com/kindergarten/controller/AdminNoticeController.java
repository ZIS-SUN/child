package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.Notice;
import com.kindergarten.service.NoticeService;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 管理员端-通知管理控制器
 */
@Api(tags = "管理员端-通知管理")
@RestController
@RequestMapping("/admin/notices")
public class AdminNoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("获取通知列表")
    @GetMapping
    public Result<Page<Notice>> getNoticeList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        try {
            Page<Notice> page = noticeService.getAllNoticeList(pageNum, pageSize, status);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取通知详情")
    @GetMapping("/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getNoticeDetail(id);
            return Result.success(notice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("创建通知")
    @PostMapping
    public Result<Void> createNotice(@RequestBody Notice notice, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            notice.setPublisherId(userId);
            notice.setStatus("DRAFT");
            noticeService.createNotice(notice);
            return Result.success("通知创建成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("更新通知")
    @PutMapping("/{id}")
    public Result<Void> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        try {
            notice.setId(id);
            noticeService.updateNotice(notice);
            return Result.success("通知更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return Result.success("通知删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("发布通知")
    @PostMapping("/{id}/publish")
    public Result<Void> publishNotice(@PathVariable Long id) {
        try {
            noticeService.publishNotice(id);
            return Result.success("通知发布成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

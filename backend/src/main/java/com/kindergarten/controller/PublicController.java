package com.kindergarten.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kindergarten.common.Result;
import com.kindergarten.entity.AlbumPhoto;
import com.kindergarten.entity.Enrollment;
import com.kindergarten.entity.Notice;
import com.kindergarten.entity.WeeklyMenu;
import com.kindergarten.service.PublicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 前台公开接口Controller
 */
@Api(tags = "前台公开接口")
@RestController
@RequestMapping("/public")
@CrossOrigin(origins = "*") // 允许跨域访问
public class PublicController {

    @Autowired
    private PublicService publicService;

    @ApiOperation("获取首页数据")
    @GetMapping("/home")
    public Result<Map<String, Object>> getHomeData() {
        Map<String, Object> data = publicService.getHomeData();
        return Result.success(data);
    }

    @ApiOperation("获取公告列表")
    @GetMapping("/notices")
    public Result<IPage<Notice>> getNoticeList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("搜索关键字") @RequestParam(required = false) String keyword
    ) {
        IPage<Notice> result = publicService.getNoticeList(page, pageSize, keyword);
        return Result.success(result);
    }

    @ApiOperation("获取公告详情")
    @GetMapping("/notices/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        Notice notice = publicService.getNoticeDetail(id);
        if (notice == null) {
            return Result.error("公告不存在或已下线");
        }
        return Result.success(notice);
    }

    @ApiOperation("获取每周食谱")
    @GetMapping("/menu/weekly")
    public Result<List<WeeklyMenu>> getWeeklyMenu(
            @ApiParam("周偏移量，0表示本周，-1表示上周，1表示下周") @RequestParam(defaultValue = "0") Integer weekOffset
    ) {
        List<WeeklyMenu> menu = publicService.getWeeklyMenuByOffset(weekOffset);
        return Result.success(menu);
    }

    @ApiOperation("获取教师列表")
    @GetMapping("/teachers")
    public Result<List<?>> getTeacherList(
            @ApiParam("班级类型：all/admin/senior/middle/junior/special") @RequestParam(defaultValue = "all") String classType
    ) {
        List<?> teachers = publicService.getTeacherList(classType);
        return Result.success(teachers);
    }

    @ApiOperation("获取相册列表")
    @GetMapping("/albums")
    public Result<List<AlbumPhoto>> getAlbumList(
            @ApiParam("分类：all/activity/classroom/playground/meal") @RequestParam(defaultValue = "all") String category
    ) {
        List<AlbumPhoto> albums = publicService.getAlbumList(category);
        return Result.success(albums);
    }

    @ApiOperation("提交在线报名")
    @PostMapping("/enrollment")
    public Result<Void> submitEnrollment(@RequestBody Enrollment enrollment) {
        try {
            publicService.submitEnrollment(enrollment);
            return Result.success("报名成功，我们会尽快与您联系");
        } catch (Exception e) {
            return Result.error("报名失败：" + e.getMessage());
        }
    }
}

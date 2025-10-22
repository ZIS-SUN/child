package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.ClassInfo;
import com.kindergarten.entity.TeacherClassRelation;
import com.kindergarten.service.ClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员端-班级管理控制器
 */
@Api(tags = "管理员端-班级管理")
@RestController
@RequestMapping("/admin/classes")
public class AdminClassController {

    @Autowired
    private ClassService classService;

    @ApiOperation("获取班级列表")
    @GetMapping
    public Result<Page<ClassInfo>> getClassList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<ClassInfo> page = classService.getClassList(pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取所有班级（不分页）")
    @GetMapping("/all")
    public Result<List<ClassInfo>> getAllClasses() {
        try {
            List<ClassInfo> classes = classService.getAllClasses();
            return Result.success(classes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取班级详情")
    @GetMapping("/{id}")
    public Result<ClassInfo> getClassDetail(@PathVariable Long id) {
        try {
            ClassInfo classInfo = classService.getClassDetail(id);
            return Result.success(classInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("创建班级")
    @PostMapping
    public Result<Void> createClass(@RequestBody ClassInfo classInfo) {
        try {
            classInfo.setStatus(1);
            classService.createClass(classInfo);
            return Result.success("班级创建成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("更新班级")
    @PutMapping("/{id}")
    public Result<Void> updateClass(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        try {
            classInfo.setId(id);
            classService.updateClass(classInfo);
            return Result.success("班级更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("删除班级")
    @DeleteMapping("/{id}")
    public Result<Void> deleteClass(@PathVariable Long id) {
        try {
            classService.deleteClass(id);
            return Result.success("班级删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("为班级分配教师")
    @PostMapping("/{id}/teachers")
    public Result<Void> assignTeachers(@PathVariable Long id, @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> teacherIds = request.get("teacherIds");
            classService.assignTeachers(id, teacherIds);
            return Result.success("教师分配成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取班级的教师列表")
    @GetMapping("/{id}/teachers")
    public Result<List<TeacherClassRelation>> getClassTeachers(@PathVariable Long id) {
        try {
            List<TeacherClassRelation> teachers = classService.getClassTeachers(id);
            return Result.success(teachers);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

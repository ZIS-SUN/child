package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.dto.ChildDTO;
import com.kindergarten.entity.ChildInfo;
import com.kindergarten.service.ChildService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员端-幼儿管理
 */
@Api(tags = "管理员端-幼儿管理")
@RestController
@RequestMapping("/admin/children")
public class AdminChildController {

    @Autowired
    private ChildService childService;

    @ApiOperation("获取幼儿列表")
    @GetMapping("")
    public Result<Page<ChildInfo>> getChildList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String name) {
        Page<ChildInfo> page = childService.getChildList(pageNum, pageSize, classId, name);
        return Result.success(page);
    }

    @ApiOperation("新增幼儿")
    @PostMapping("")
    public Result<Void> addChild(@Validated @RequestBody ChildDTO dto) {
        childService.addChild(dto);
        return Result.success("幼儿添加成功");
    }

    @ApiOperation("更新幼儿信息")
    @PutMapping("/{id}")
    public Result<Void> updateChild(@PathVariable Long id, @Validated @RequestBody ChildDTO dto) {
        childService.updateChild(id, dto);
        return Result.success("幼儿信息更新成功");
    }

    @ApiOperation("删除幼儿")
    @DeleteMapping("/{id}")
    public Result<Void> deleteChild(@PathVariable Long id) {
        childService.deleteChild(id);
        return Result.success("幼儿删除成功");
    }
}

package com.kindergarten.controller;

import com.kindergarten.common.Result;
import com.kindergarten.service.ParentService;
import com.kindergarten.vo.ChildDetailVO;
import com.kindergarten.vo.HomeDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家长端-首页和孩子信息
 */
@Api(tags = "家长端-首页和孩子信息")
@RestController
@RequestMapping("/parent")
public class ParentHomeController {

    @Autowired
    private ParentService parentService;

    @ApiOperation("获取首页数据")
    @GetMapping("/home")
    public Result<HomeDataVO> getHomeData(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        HomeDataVO data = parentService.getHomeData(userId);
        return Result.success(data);
    }

    @ApiOperation("获取我的孩子列表")
    @GetMapping("/children")
    public Result<List<ChildDetailVO>> getMyChildren(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<ChildDetailVO> children = parentService.getMyChildren(userId);
        return Result.success(children);
    }

    @ApiOperation("获取孩子详细信息")
    @GetMapping("/children/{id}")
    public Result<ChildDetailVO> getChildDetail(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ChildDetailVO detail = parentService.getChildDetail(userId, id);
        return Result.success(detail);
    }
}

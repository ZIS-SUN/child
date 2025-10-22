package com.kindergarten.controller;

import com.kindergarten.common.Result;
import com.kindergarten.service.AdminService;
import com.kindergarten.vo.DashboardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员端-数据面板
 */
@Api(tags = "管理员端-数据面板")
@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;

    @ApiOperation("获取数据面板")
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        DashboardVO data = adminService.getDashboard();
        return Result.success(data);
    }
}

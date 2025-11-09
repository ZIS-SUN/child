package com.kindergarten.controller;

import com.kindergarten.common.Result;
import com.kindergarten.entity.ChildHealth;
import com.kindergarten.entity.GrowthRecord;
import com.kindergarten.entity.TemperatureRecord;
import com.kindergarten.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 家长端健康信息控制器
 */
@RestController
@RequestMapping("/parent/health")
@RequiredArgsConstructor
public class ParentHealthController {

    private final HealthService healthService;

    /**
     * 获取孩子健康信息
     */
    @GetMapping("/{childId}")
    public Result<ChildHealth> getHealthInfo(@PathVariable Long childId) {
        // TODO: 验证是否为该家长的孩子
        ChildHealth health = healthService.getChildHealth(childId);
        return Result.success(health);
    }

    /**
     * 获取孩子体温记录
     */
    @GetMapping("/{childId}/temperature")
    public Result<List<TemperatureRecord>> getTemperatureRecords(
            @PathVariable Long childId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        // TODO: 验证是否为该家长的孩子
        List<TemperatureRecord> records = healthService.getTemperatureRecords(childId, startDate, endDate);
        return Result.success(records);
    }

    /**
     * 获取孩子成长记录
     */
    @GetMapping("/{childId}/growth")
    public Result<List<GrowthRecord>> getGrowthRecords(@PathVariable Long childId) {
        // TODO: 验证是否为该家长的孩子
        List<GrowthRecord> records = healthService.getGrowthRecords(childId);
        return Result.success(records);
    }

    /**
     * 获取最近7天体温记录
     */
    @GetMapping("/{childId}/temperature/recent")
    public Result<List<TemperatureRecord>> getRecentTemperature(@PathVariable Long childId) {
        // TODO: 验证是否为该家长的孩子
        List<TemperatureRecord> records = healthService.getRecentTemperature(childId, 7);
        return Result.success(records);
    }
}

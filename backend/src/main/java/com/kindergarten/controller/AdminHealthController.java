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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理端健康信息控制器
 */
@RestController
@RequestMapping("/api/admin/health")
@RequiredArgsConstructor
public class AdminHealthController {

    private final HealthService healthService;

    /**
     * 获取幼儿健康信息
     */
    @GetMapping("/{childId}")
    public Result<ChildHealth> getHealthInfo(@PathVariable Long childId) {
        ChildHealth health = healthService.getChildHealth(childId);
        return Result.success(health);
    }

    /**
     * 更新健康基本信息
     */
    @PutMapping("/{childId}")
    public Result<Void> updateHealthInfo(
            @PathVariable Long childId,
            @RequestBody Map<String, String> data) {
        healthService.updateHealthInfo(
                childId,
                data.get("allergyInfo"),
                data.get("medicalHistory"),
                data.get("bloodType")
        );
        return Result.success();
    }

    /**
     * 记录体温
     */
    @PostMapping("/{childId}/temperature")
    public Result<Void> recordTemperature(
            @PathVariable Long childId,
            @RequestBody Map<String, Object> data) {
        
        Double temperature = Double.valueOf(data.get("temperature").toString());
        LocalDateTime measureTime = LocalDateTime.parse(data.get("measureTime").toString());
        String period = (String) data.get("period");
        String remark = (String) data.get("remark");
        Long recorderId = Long.valueOf(data.get("recorderId").toString());
        
        healthService.recordTemperature(childId, temperature, measureTime, period, remark, recorderId);
        return Result.success();
    }

    /**
     * 获取体温记录
     */
    @GetMapping("/{childId}/temperature")
    public Result<List<TemperatureRecord>> getTemperatureRecords(
            @PathVariable Long childId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<TemperatureRecord> records = healthService.getTemperatureRecords(childId, startDate, endDate);
        return Result.success(records);
    }

    /**
     * 删除体温记录
     */
    @DeleteMapping("/temperature/{id}")
    public Result<Void> deleteTemperatureRecord(@PathVariable Long id) {
        healthService.deleteTemperatureRecord(id);
        return Result.success();
    }

    /**
     * 记录身高体重
     */
    @PostMapping("/{childId}/growth")
    public Result<Void> recordGrowth(
            @PathVariable Long childId,
            @RequestBody Map<String, Object> data) {
        
        Double height = data.get("height") != null ? Double.valueOf(data.get("height").toString()) : null;
        Double weight = data.get("weight") != null ? Double.valueOf(data.get("weight").toString()) : null;
        LocalDate measureDate = LocalDate.parse(data.get("measureDate").toString());
        String remark = (String) data.get("remark");
        Long recorderId = Long.valueOf(data.get("recorderId").toString());
        
        healthService.recordGrowth(childId, height, weight, measureDate, remark, recorderId);
        return Result.success();
    }

    /**
     * 获取成长记录
     */
    @GetMapping("/{childId}/growth")
    public Result<List<GrowthRecord>> getGrowthRecords(@PathVariable Long childId) {
        List<GrowthRecord> records = healthService.getGrowthRecords(childId);
        return Result.success(records);
    }

    /**
     * 删除成长记录
     */
    @DeleteMapping("/growth/{id}")
    public Result<Void> deleteGrowthRecord(@PathVariable Long id) {
        healthService.deleteGrowthRecord(id);
        return Result.success();
    }
}

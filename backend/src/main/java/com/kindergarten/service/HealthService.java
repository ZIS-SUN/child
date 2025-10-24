package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kindergarten.entity.ChildHealth;
import com.kindergarten.entity.GrowthRecord;
import com.kindergarten.entity.TemperatureRecord;
import com.kindergarten.mapper.ChildHealthMapper;
import com.kindergarten.mapper.GrowthRecordMapper;
import com.kindergarten.mapper.TemperatureRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康信息服务类
 */
@Service
@RequiredArgsConstructor
public class HealthService {

    private final ChildHealthMapper childHealthMapper;
    private final TemperatureRecordMapper temperatureRecordMapper;
    private final GrowthRecordMapper growthRecordMapper;

    /**
     * 获取幼儿健康信息
     */
    public ChildHealth getChildHealth(Long childId) {
        QueryWrapper<ChildHealth> wrapper = new QueryWrapper<>();
        wrapper.eq("child_id", childId);
        ChildHealth health = childHealthMapper.selectOne(wrapper);
        
        // 如果不存在,创建一个新的
        if (health == null) {
            health = new ChildHealth();
            health.setChildId(childId);
            childHealthMapper.insert(health);
        }
        
        return health;
    }

    /**
     * 更新健康基本信息
     */
    @Transactional
    public void updateHealthInfo(Long childId, String allergyInfo, String medicalHistory, String bloodType) {
        ChildHealth health = getChildHealth(childId);
        health.setAllergyInfo(allergyInfo);
        health.setMedicalHistory(medicalHistory);
        health.setBloodType(bloodType);
        childHealthMapper.updateById(health);
    }

    /**
     * 记录体温
     */
    @Transactional
    public void recordTemperature(Long childId, Double temperature, LocalDateTime measureTime, 
                                   String period, String remark, Long recorderId) {
        TemperatureRecord record = new TemperatureRecord();
        record.setChildId(childId);
        record.setTemperature(temperature);
        record.setMeasureTime(measureTime);
        record.setPeriod(period);
        record.setRemark(remark);
        record.setRecorderId(recorderId);
        temperatureRecordMapper.insert(record);
    }

    /**
     * 获取体温记录
     */
    public List<TemperatureRecord> getTemperatureRecords(Long childId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<TemperatureRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("child_id", childId);
        
        if (startDate != null) {
            wrapper.ge("measure_time", startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le("measure_time", endDate.plusDays(1).atStartOfDay());
        }
        
        wrapper.orderByDesc("measure_time");
        return temperatureRecordMapper.selectList(wrapper);
    }

    /**
     * 删除体温记录
     */
    @Transactional
    public void deleteTemperatureRecord(Long id) {
        temperatureRecordMapper.deleteById(id);
    }

    /**
     * 记录身高体重
     */
    @Transactional
    public void recordGrowth(Long childId, Double height, Double weight, LocalDate measureDate, 
                            String remark, Long recorderId) {
        GrowthRecord record = new GrowthRecord();
        record.setChildId(childId);
        record.setHeight(height);
        record.setWeight(weight);
        record.setMeasureDate(measureDate);
        record.setRemark(remark);
        record.setRecorderId(recorderId);
        growthRecordMapper.insert(record);
    }

    /**
     * 获取成长记录
     */
    public List<GrowthRecord> getGrowthRecords(Long childId) {
        QueryWrapper<GrowthRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("child_id", childId);
        wrapper.orderByAsc("measure_date");
        return growthRecordMapper.selectList(wrapper);
    }

    /**
     * 删除成长记录
     */
    @Transactional
    public void deleteGrowthRecord(Long id) {
        growthRecordMapper.deleteById(id);
    }

    /**
     * 获取最近的体温记录
     */
    public List<TemperatureRecord> getRecentTemperature(Long childId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return getTemperatureRecords(childId, startDate, LocalDate.now());
    }
}

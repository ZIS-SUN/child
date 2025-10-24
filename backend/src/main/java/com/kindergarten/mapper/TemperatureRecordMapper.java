package com.kindergarten.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kindergarten.entity.TemperatureRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 体温记录Mapper
 */
@Mapper
public interface TemperatureRecordMapper extends BaseMapper<TemperatureRecord> {
}

package com.kindergarten.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kindergarten.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 在线报名Mapper
 */
@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
}

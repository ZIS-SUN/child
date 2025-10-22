package com.kindergarten.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kindergarten.entity.TeacherInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师信息Mapper
 */
@Mapper
public interface TeacherInfoMapper extends BaseMapper<TeacherInfo> {
}

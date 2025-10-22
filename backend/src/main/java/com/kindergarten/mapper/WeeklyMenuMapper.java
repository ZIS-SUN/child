package com.kindergarten.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kindergarten.entity.WeeklyMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 每周食谱Mapper
 */
@Mapper
public interface WeeklyMenuMapper extends BaseMapper<WeeklyMenu> {
}

package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.dto.ChildDTO;
import com.kindergarten.entity.ChildInfo;
import com.kindergarten.mapper.ChildInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 幼儿管理服务
 */
@Service
public class ChildService {

    @Autowired
    private ChildInfoMapper childInfoMapper;

    /**
     * 获取幼儿列表
     */
    public Page<ChildInfo> getChildList(Integer pageNum, Integer pageSize, Long classId, String name) {
        Page<ChildInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ChildInfo> wrapper = new LambdaQueryWrapper<>();

        if (classId != null) {
            wrapper.eq(ChildInfo::getClassId, classId);
        }

        if (name != null && !name.isEmpty()) {
            wrapper.like(ChildInfo::getName, name);
        }

        wrapper.orderByDesc(ChildInfo::getCreatedTime);
        return childInfoMapper.selectPage(page, wrapper);
    }

    /**
     * 新增幼儿
     */
    @Transactional(rollbackFor = Exception.class)
    public void addChild(ChildDTO dto) {
        ChildInfo child = new ChildInfo();
        BeanUtils.copyProperties(dto, child);
        child.setStatus(1);

        childInfoMapper.insert(child);
    }

    /**
     * 更新幼儿信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateChild(Long id, ChildDTO dto) {
        ChildInfo child = childInfoMapper.selectById(id);
        if (child == null) {
            throw new RuntimeException("幼儿信息不存在");
        }

        BeanUtils.copyProperties(dto, child);
        childInfoMapper.updateById(child);
    }

    /**
     * 删除幼儿
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteChild(Long id) {
        childInfoMapper.deleteById(id);
    }
}

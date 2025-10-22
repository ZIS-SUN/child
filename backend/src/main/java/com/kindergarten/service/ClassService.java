package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.entity.ClassInfo;
import com.kindergarten.entity.TeacherClassRelation;
import com.kindergarten.mapper.ClassInfoMapper;
import com.kindergarten.mapper.TeacherClassRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 班级服务
 */
@Service
public class ClassService {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Autowired
    private TeacherClassRelationMapper teacherClassRelationMapper;

    /**
     * 获取班级列表（分页）
     */
    public Page<ClassInfo> getClassList(Integer pageNum, Integer pageSize) {
        Page<ClassInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClassInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ClassInfo::getCreatedTime);
        return classInfoMapper.selectPage(page, wrapper);
    }

    /**
     * 获取所有班级列表（不分页）
     */
    public List<ClassInfo> getAllClasses() {
        return classInfoMapper.selectList(null);
    }

    /**
     * 获取班级详情
     */
    public ClassInfo getClassDetail(Long id) {
        return classInfoMapper.selectById(id);
    }

    /**
     * 创建班级
     */
    @Transactional(rollbackFor = Exception.class)
    public void createClass(ClassInfo classInfo) {
        classInfoMapper.insert(classInfo);
    }

    /**
     * 更新班级
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateClass(ClassInfo classInfo) {
        classInfoMapper.updateById(classInfo);
    }

    /**
     * 删除班级
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteClass(Long id) {
        classInfoMapper.deleteById(id);
        // 删除教师-班级关系
        LambdaQueryWrapper<TeacherClassRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherClassRelation::getClassId, id);
        teacherClassRelationMapper.delete(wrapper);
    }

    /**
     * 为班级分配教师
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignTeachers(Long classId, List<Long> teacherIds) {
        // 先删除该班级的所有教师关系
        LambdaQueryWrapper<TeacherClassRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherClassRelation::getClassId, classId);
        teacherClassRelationMapper.delete(wrapper);

        // 添加新的教师关系
        for (int i = 0; i < teacherIds.size(); i++) {
            TeacherClassRelation relation = new TeacherClassRelation();
            relation.setTeacherId(teacherIds.get(i));
            relation.setClassId(classId);
            // 第一个教师设为班主任
            relation.setIsMainTeacher(i == 0 ? 1 : 0);
            teacherClassRelationMapper.insert(relation);
        }
    }

    /**
     * 获取班级的教师列表
     */
    public List<TeacherClassRelation> getClassTeachers(Long classId) {
        LambdaQueryWrapper<TeacherClassRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherClassRelation::getClassId, classId);
        return teacherClassRelationMapper.selectList(wrapper);
    }
}

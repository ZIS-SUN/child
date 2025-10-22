package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师信息实体类（用于前台展示）
 */
@Data
@TableName("teacher_info")
public class TeacherInfo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId; // 关联sys_user表

    private String name; // 姓名

    private String position; // 职位

    private String avatar; // 头像URL

    private String classType; // 班级类型: admin/senior/middle/junior/special

    private String className; // 班级名称

    private String tags; // 资质标签 (JSON数组)

    private String description; // 个人简介

    private Integer sortOrder; // 排序

    private Integer displayOnHome; // 是否在首页显示: 0-否 1-是

    private Integer status; // 状态: 0-隐藏 1-显示

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

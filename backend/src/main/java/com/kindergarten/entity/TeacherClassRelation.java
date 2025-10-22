package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师-班级关系实体类
 */
@Data
@TableName("teacher_class_relation")
public class TeacherClassRelation implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long teacherId;

    private Long classId;

    private Integer isMainTeacher;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}

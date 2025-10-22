package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级信息实体类
 */
@Data
@TableName("class")
public class ClassInfo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String className;

    private String classType;

    private Integer capacity;

    private String classroom;

    private Long teacherId;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

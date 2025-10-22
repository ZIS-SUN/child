package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 家长-孩子关系实体类
 */
@Data
@TableName("parent_child_relation")
public class ParentChildRelation implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private Long childId;

    private Integer isPrimary;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}

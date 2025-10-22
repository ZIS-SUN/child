package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 家园互动实体类
 */
@Data
@TableName("interaction")
public class Interaction implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private Long teacherId;

    private Long childId;

    private String message;

    private String reply;

    private LocalDateTime replyTime;

    private Integer isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}

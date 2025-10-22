package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 成长相册实体类
 */
@Data
@TableName("album")
public class Album implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long classId;

    private String title;

    private String description;

    private String imageUrls;

    private Long publisherId;

    private LocalDateTime publishTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}

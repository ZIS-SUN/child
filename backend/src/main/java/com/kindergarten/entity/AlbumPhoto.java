package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 相册照片实体类
 */
@Data
@TableName("album_photo")
public class AlbumPhoto implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title; // 照片标题

    private String category; // 分类: activity/classroom/playground/meal

    private String imageUrl; // 图片URL

    private String description; // 描述

    private LocalDateTime photoDate; // 拍摄日期

    private Integer sortOrder; // 排序

    private Integer status; // 状态: 0-隐藏 1-显示

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

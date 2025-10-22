package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每周食谱实体类
 */
@Data
@TableName("weekly_menu")
public class WeeklyMenu implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer weekday; // 1-5 (周一到周五)

    private LocalDate menuDate; // 食谱日期

    private String breakfast; // 早餐 (JSON格式)

    private String snack1; // 上午加餐 (JSON格式)

    private String lunch; // 午餐 (JSON格式)

    private String snack2; // 下午点心 (JSON格式)

    private Integer weekOffset; // 周偏移量，0表示本周，-1表示上周，1表示下周

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体温记录实体类
 */
@Data
@TableName("temperature_record")
public class TemperatureRecord implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 体温值(摄氏度)
     */
    private Double temperature;

    /**
     * 测量时间
     */
    private LocalDateTime measureTime;

    /**
     * 测量时段(早上/中午/下午/晚上)
     */
    private String period;

    /**
     * 备注
     */
    private String remark;

    /**
     * 记录人ID
     */
    private Long recorderId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

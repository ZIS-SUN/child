package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 成长记录实体类(身高体重)
 */
@Data
@TableName("growth_record")
public class GrowthRecord implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 身高(cm)
     */
    private Double height;

    /**
     * 体重(kg)
     */
    private Double weight;

    /**
     * 测量日期
     */
    private LocalDate measureDate;

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

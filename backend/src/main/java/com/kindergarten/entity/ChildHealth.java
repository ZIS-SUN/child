package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 幼儿健康信息实体类
 */
@Data
@TableName("child_health")
public class ChildHealth implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long childId;

    private String allergyInfo;

    private String medicalHistory;

    private String bloodType;

    private String temperatureRecord;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

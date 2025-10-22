package com.kindergarten.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 在线报名实体类
 */
@Data
@TableName("enrollment")
public class Enrollment implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 孩子信息
    private String childName;

    private String childGender;

    private LocalDate childBirthday;

    private String childIdCard;

    // 家长信息
    private String parentName;

    private String relationship; // 与孩子关系

    private String phone;

    private String address;

    // 报名信息
    private String intentClass; // 意向班级

    private String remark; // 备注

    private String attachments; // 附件URL (JSON格式)

    private String status; // 状态: PENDING-待处理, CONTACTED-已联系, ACCEPTED-已录取, REJECTED-已拒绝

    private Long processedBy; // 处理人ID

    private LocalDateTime processedTime; // 处理时间

    private String processNote; // 处理备注

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

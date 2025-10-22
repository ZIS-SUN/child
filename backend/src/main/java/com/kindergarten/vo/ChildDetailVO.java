package com.kindergarten.vo;

import lombok.Data;
import java.time.LocalDate;

/**
 * 孩子详细信息VO
 */
@Data
public class ChildDetailVO {
    private Long id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String idNumber;
    private Long classId;
    private String className;
    private String classroom;
    private String teacherName;
    private LocalDate enrollmentDate;
    private Integer status;
    private String avatarUrl;
    private Integer isPrimary;
}

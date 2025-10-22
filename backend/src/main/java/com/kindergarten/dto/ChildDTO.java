package com.kindergarten.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * 幼儿DTO
 */
@Data
public class ChildDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String gender;

    private LocalDate birthDate;

    private String idNumber;

    private Long classId;

    private LocalDate enrollmentDate;

    private String avatarUrl;
}

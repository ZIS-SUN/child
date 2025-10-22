package com.kindergarten.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 请假申请DTO
 */
@Data
public class LeaveRequestDTO {

    @NotNull(message = "孩子ID不能为空")
    private Long childId;

    @NotNull(message = "请假类型不能为空")
    private String leaveType;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    private String reason;
}

package com.kindergarten.vo;

import lombok.Data;

/**
 * 数据面板VO
 */
@Data
public class DashboardVO {
    private Integer totalChildren;
    private Integer totalClasses;
    private Integer attendanceRate;
    private Integer pendingLeave;
    private Integer unreadMessages;
}

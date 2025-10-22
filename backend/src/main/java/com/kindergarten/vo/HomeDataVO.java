package com.kindergarten.vo;

import com.kindergarten.entity.Notice;
import lombok.Data;
import java.util.List;

/**
 * 家长端首页数据VO
 */
@Data
public class HomeDataVO {
    private Integer childrenCount;
    private Integer unreadNotices;
    private Integer pendingLeave;
    private Integer attendanceRate;
    private List<Notice> recentNotices;
}

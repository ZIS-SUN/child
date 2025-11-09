package com.kindergarten.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kindergarten.common.Result;
import com.kindergarten.entity.Attendance;
import com.kindergarten.entity.ChildInfo;
import com.kindergarten.entity.ParentChildRelation;
import com.kindergarten.entity.ParentInfo;
import com.kindergarten.mapper.ChildInfoMapper;
import com.kindergarten.mapper.ParentChildRelationMapper;
import com.kindergarten.mapper.ParentInfoMapper;
import com.kindergarten.service.AttendanceService;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 家长端-考勤查询
 */
@Api(tags = "家长端-考勤查询")
@RestController
@RequestMapping("/parent/attendance")
public class ParentAttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ParentChildRelationMapper parentChildRelationMapper;

    @Autowired
    private ChildInfoMapper childInfoMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ParentInfoMapper parentInfoMapper;

    @ApiOperation("获取孩子的考勤日历")
    @GetMapping("/calendar")
    public Result<List<Attendance>> getAttendanceCalendar(
            @RequestParam Long childId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            HttpServletRequest request) {
        // 验证家长权限
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        if (!isParentOfChild(userId, childId)) {
            return Result.error("无权查看该幼儿信息");
        }

        List<Attendance> records = attendanceService.getMonthlyAttendance(childId, year, month);
        return Result.success(records);
    }

    @ApiOperation("获取孩子的考勤统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getAttendanceStats(
            @RequestParam Long childId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            HttpServletRequest request) {
        // 验证家长权限
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        if (!isParentOfChild(userId, childId)) {
            return Result.error("无权查看该幼儿信息");
        }

        Map<String, Object> stats = attendanceService.getAttendanceStats(childId, year, month);

        // 添加孩子基本信息
        ChildInfo child = childInfoMapper.selectById(childId);
        if (child != null) {
            stats.put("childName", child.getName());
            stats.put("childAvatar", child.getAvatarUrl());
        }

        return Result.success(stats);
    }

    /**
     * 验证家长是否有权查看该幼儿信息
     */
    private boolean isParentOfChild(Long userId, Long childId) {
        // 先通过userId获取parentInfoId
        Long parentInfoId = getParentInfoId(userId);
        if (parentInfoId == null) {
            return false;
        }
        
        LambdaQueryWrapper<ParentChildRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentChildRelation::getParentId, parentInfoId)
               .eq(ParentChildRelation::getChildId, childId);
        return parentChildRelationMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 通过userId获取parentInfoId
     */
    private Long getParentInfoId(Long userId) {
        LambdaQueryWrapper<ParentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentInfo::getUserId, userId);
        ParentInfo parentInfo = parentInfoMapper.selectOne(wrapper);
        return parentInfo != null ? parentInfo.getId() : null;
    }
}

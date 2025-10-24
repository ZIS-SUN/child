package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.entity.Attendance;
import com.kindergarten.entity.ChildInfo;
import com.kindergarten.mapper.AttendanceMapper;
import com.kindergarten.mapper.ChildInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤管理服务
 */
@Service
public class AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private ChildInfoMapper childInfoMapper;

    /**
     * 获取考勤记录列表（分页）
     */
    public Page<Attendance> getAttendanceList(Integer pageNum, Integer pageSize,
                                               Long childId, Long classId,
                                               LocalDate startDate, LocalDate endDate,
                                               String status) {
        Page<Attendance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();

        if (childId != null) {
            wrapper.eq(Attendance::getChildId, childId);
        }

        // 如果提供了班级ID，需要查询该班级下的所有幼儿
        if (classId != null) {
            LambdaQueryWrapper<ChildInfo> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(ChildInfo::getClassId, classId);
            List<ChildInfo> children = childInfoMapper.selectList(childWrapper);
            List<Long> childIds = children.stream().map(ChildInfo::getId).collect(java.util.stream.Collectors.toList());
            if (!childIds.isEmpty()) {
                wrapper.in(Attendance::getChildId, childIds);
            } else {
                // 如果班级下没有幼儿，返回空结果
                return page;
            }
        }

        if (startDate != null) {
            wrapper.ge(Attendance::getAttendanceDate, startDate);
        }

        if (endDate != null) {
            wrapper.le(Attendance::getAttendanceDate, endDate);
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq(Attendance::getStatus, status);
        }

        wrapper.orderByDesc(Attendance::getAttendanceDate, Attendance::getCreatedTime);
        return attendanceMapper.selectPage(page, wrapper);
    }

    /**
     * 获取某个幼儿的考勤记录
     */
    public List<Attendance> getChildAttendance(Long childId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getChildId, childId);

        if (startDate != null) {
            wrapper.ge(Attendance::getAttendanceDate, startDate);
        }

        if (endDate != null) {
            wrapper.le(Attendance::getAttendanceDate, endDate);
        }

        wrapper.orderByAsc(Attendance::getAttendanceDate);
        return attendanceMapper.selectList(wrapper);
    }

    /**
     * 获取月度考勤日历数据
     */
    public List<Attendance> getMonthlyAttendance(Long childId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return getChildAttendance(childId, startDate, endDate);
    }

    /**
     * 签到
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long childId, Long recorderId, String remark) {
        LocalDate today = LocalDate.now();

        // 检查今天是否已经有考勤记录
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getChildId, childId)
               .eq(Attendance::getAttendanceDate, today);
        Attendance existing = attendanceMapper.selectOne(wrapper);

        if (existing != null) {
            throw new RuntimeException("今日已签到，不可重复签到");
        }

        // 创建签到记录
        Attendance attendance = new Attendance();
        attendance.setChildId(childId);
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus("present"); // present-出勤, absent-缺勤, late-迟到
        attendance.setRecorderId(recorderId);
        attendance.setRemark(remark);

        attendanceMapper.insert(attendance);
    }

    /**
     * 签退
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkOut(Long childId, String remark) {
        LocalDate today = LocalDate.now();

        // 查找今天的考勤记录
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getChildId, childId)
               .eq(Attendance::getAttendanceDate, today);
        Attendance attendance = attendanceMapper.selectOne(wrapper);

        if (attendance == null) {
            throw new RuntimeException("未找到今日签到记录");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("今日已签退，不可重复签退");
        }

        // 更新签退时间
        attendance.setCheckOutTime(LocalDateTime.now());
        if (remark != null && !remark.isEmpty()) {
            attendance.setRemark(attendance.getRemark() + "; " + remark);
        }

        attendanceMapper.updateById(attendance);
    }

    /**
     * 记录缺勤
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordAbsent(Long childId, LocalDate date, Long recorderId, String remark) {
        // 检查是否已有记录
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getChildId, childId)
               .eq(Attendance::getAttendanceDate, date);
        Attendance existing = attendanceMapper.selectOne(wrapper);

        if (existing != null) {
            throw new RuntimeException("该日期已有考勤记录");
        }

        Attendance attendance = new Attendance();
        attendance.setChildId(childId);
        attendance.setAttendanceDate(date);
        attendance.setStatus("absent");
        attendance.setRecorderId(recorderId);
        attendance.setRemark(remark);

        attendanceMapper.insert(attendance);
    }

    /**
     * 更新考勤记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttendance(Long id, LocalDateTime checkInTime, LocalDateTime checkOutTime,
                                  String status, String remark) {
        Attendance attendance = attendanceMapper.selectById(id);
        if (attendance == null) {
            throw new RuntimeException("考勤记录不存在");
        }

        if (checkInTime != null) {
            attendance.setCheckInTime(checkInTime);
        }
        if (checkOutTime != null) {
            attendance.setCheckOutTime(checkOutTime);
        }
        if (status != null) {
            attendance.setStatus(status);
        }
        if (remark != null) {
            attendance.setRemark(remark);
        }

        attendanceMapper.updateById(attendance);
    }

    /**
     * 删除考勤记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttendance(Long id) {
        attendanceMapper.deleteById(id);
    }

    /**
     * 获取考勤统计
     */
    public Map<String, Object> getAttendanceStats(Long childId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Attendance> records = getChildAttendance(childId, startDate, endDate);

        long presentCount = records.stream().filter(r -> "present".equals(r.getStatus())).count();
        long absentCount = records.stream().filter(r -> "absent".equals(r.getStatus())).count();
        long lateCount = records.stream().filter(r -> "late".equals(r.getStatus())).count();
        long totalCount = records.size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("presentCount", presentCount);
        stats.put("absentCount", absentCount);
        stats.put("lateCount", lateCount);
        stats.put("totalCount", totalCount);
        stats.put("attendanceRate", totalCount > 0 ? String.format("%.2f%%", (presentCount + lateCount) * 100.0 / totalCount) : "0.00%");

        return stats;
    }

    /**
     * 获取班级考勤统计
     */
    public Map<String, Object> getClassAttendanceStats(Long classId, LocalDate date) {
        // 获取班级所有幼儿
        LambdaQueryWrapper<ChildInfo> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(ChildInfo::getClassId, classId)
                    .eq(ChildInfo::getStatus, 1);
        List<ChildInfo> children = childInfoMapper.selectList(childWrapper);
        long totalChildren = children.size();

        // 获取今日考勤记录
        List<Long> childIds = children.stream().map(ChildInfo::getId).collect(java.util.stream.Collectors.toList());

        long presentCount = 0;
        long absentCount = 0;
        long lateCount = 0;
        long notRecordedCount = totalChildren;

        if (!childIds.isEmpty()) {
            LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Attendance::getChildId, childIds)
                   .eq(Attendance::getAttendanceDate, date);
            List<Attendance> records = attendanceMapper.selectList(wrapper);

            presentCount = records.stream().filter(r -> "present".equals(r.getStatus())).count();
            absentCount = records.stream().filter(r -> "absent".equals(r.getStatus())).count();
            lateCount = records.stream().filter(r -> "late".equals(r.getStatus())).count();
            notRecordedCount = totalChildren - records.size();
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalChildren", totalChildren);
        stats.put("presentCount", presentCount);
        stats.put("absentCount", absentCount);
        stats.put("lateCount", lateCount);
        stats.put("notRecordedCount", notRecordedCount);
        stats.put("attendanceRate", totalChildren > 0 ? String.format("%.2f%%", (presentCount + lateCount) * 100.0 / totalChildren) : "0.00%");

        return stats;
    }
}

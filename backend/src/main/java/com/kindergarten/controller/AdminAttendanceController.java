package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.Attendance;
import com.kindergarten.service.AttendanceService;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理员端-考勤管理
 */
@Api(tags = "管理员端-考勤管理")
@RestController
@RequestMapping("/admin/attendance")
public class AdminAttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("获取考勤记录列表")
    @GetMapping("")
    public Result<Page<Attendance>> getAttendanceList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long childId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status) {
        Page<Attendance> page = attendanceService.getAttendanceList(
                pageNum, pageSize, childId, classId, startDate, endDate, status);
        return Result.success(page);
    }

    @ApiOperation("签到")
    @PostMapping("/check-in")
    public Result<Void> checkIn(@RequestParam Long childId,
                                 @RequestParam(required = false) String remark,
                                 HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long recorderId = jwtUtil.getUserIdFromToken(token);

        attendanceService.checkIn(childId, recorderId, remark);
        return Result.success("签到成功");
    }

    @ApiOperation("签退")
    @PostMapping("/check-out")
    public Result<Void> checkOut(@RequestParam Long childId,
                                  @RequestParam(required = false) String remark) {
        attendanceService.checkOut(childId, remark);
        return Result.success("签退成功");
    }

    @ApiOperation("记录缺勤")
    @PostMapping("/absent")
    public Result<Void> recordAbsent(@RequestParam Long childId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      @RequestParam(required = false) String remark,
                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long recorderId = jwtUtil.getUserIdFromToken(token);

        attendanceService.recordAbsent(childId, date, recorderId, remark);
        return Result.success("缺勤记录成功");
    }

    @ApiOperation("更新考勤记录")
    @PutMapping("/{id}")
    public Result<Void> updateAttendance(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkInTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOutTime,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String remark) {
        attendanceService.updateAttendance(id, checkInTime, checkOutTime, status, remark);
        return Result.success("考勤记录更新成功");
    }

    @ApiOperation("删除考勤记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return Result.success("考勤记录删除成功");
    }

    @ApiOperation("获取班级今日考勤统计")
    @GetMapping("/stats/class/{classId}")
    public Result<Map<String, Object>> getClassAttendanceStats(
            @PathVariable Long classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        Map<String, Object> stats = attendanceService.getClassAttendanceStats(classId, date);
        return Result.success(stats);
    }

    @ApiOperation("获取幼儿月度考勤统计")
    @GetMapping("/stats/child/{childId}")
    public Result<Map<String, Object>> getChildAttendanceStats(
            @PathVariable Long childId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Map<String, Object> stats = attendanceService.getAttendanceStats(childId, year, month);
        return Result.success(stats);
    }
}

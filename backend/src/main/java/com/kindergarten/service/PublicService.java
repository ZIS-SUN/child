package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.entity.*;
import com.kindergarten.mapper.*;
import com.kindergarten.vo.HomeDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台公开服务
 */
@Service
public class PublicService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private WeeklyMenuMapper weeklyMenuMapper;

    @Autowired
    private TeacherInfoMapper teacherInfoMapper;

    @Autowired
    private AlbumPhotoMapper albumPhotoMapper;

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    /**
     * 获取首页数据
     */
    public Map<String, Object> getHomeData() {
        Map<String, Object> result = new HashMap<>();

        // 获取首页显示的教师（最多4个）
        LambdaQueryWrapper<TeacherInfo> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(TeacherInfo::getStatus, 1);
        teacherWrapper.eq(TeacherInfo::getDisplayOnHome, 1);
        teacherWrapper.orderByAsc(TeacherInfo::getSortOrder);
        teacherWrapper.last("LIMIT 4");
        List<TeacherInfo> teachers = teacherInfoMapper.selectList(teacherWrapper);
        result.put("teachers", teachers);

        // 获取最新公告（3条）
        LambdaQueryWrapper<Notice> noticeWrapper = new LambdaQueryWrapper<>();
        noticeWrapper.eq(Notice::getStatus, "PUBLISHED");
        noticeWrapper.orderByDesc(Notice::getPublishTime);
        noticeWrapper.last("LIMIT 3");
        List<Notice> notices = noticeMapper.selectList(noticeWrapper);
        result.put("notices", notices);

        // 获取本周食谱
        List<WeeklyMenu> weeklyMenu = getWeeklyMenuByOffset(0);
        result.put("menu", weeklyMenu);

        return result;
    }

    /**
     * 获取公告列表（分页）
     */
    public IPage<Notice> getNoticeList(Integer page, Integer pageSize, String keyword) {
        Page<Notice> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, "PUBLISHED");

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Notice::getTitle, keyword)
                    .or().like(Notice::getContent, keyword));
        }

        wrapper.orderByDesc(Notice::getPublishTime);
        return noticeMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取公告详情
     */
    public Notice getNoticeDetail(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice != null && "PUBLISHED".equals(notice.getStatus())) {
            // 这里可以增加浏览次数
            return notice;
        }
        return null;
    }

    /**
     * 获取每周食谱
     */
    public List<WeeklyMenu> getWeeklyMenuByOffset(Integer weekOffset) {
        if (weekOffset == null) {
            weekOffset = 0;
        }

        // 计算目标周的周一日期
        LocalDate today = LocalDate.now();
        LocalDate targetMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusWeeks(weekOffset);

        LambdaQueryWrapper<WeeklyMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(WeeklyMenu::getMenuDate, targetMonday);
        wrapper.lt(WeeklyMenu::getMenuDate, targetMonday.plusDays(5));
        wrapper.orderByAsc(WeeklyMenu::getWeekday);

        return weeklyMenuMapper.selectList(wrapper);
    }

    /**
     * 获取教师列表
     */
    public List<TeacherInfo> getTeacherList(String classType) {
        LambdaQueryWrapper<TeacherInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherInfo::getStatus, 1);

        if (StringUtils.hasText(classType) && !"all".equals(classType)) {
            wrapper.eq(TeacherInfo::getClassType, classType);
        }

        wrapper.orderByAsc(TeacherInfo::getSortOrder);
        return teacherInfoMapper.selectList(wrapper);
    }

    /**
     * 获取相册列表
     */
    public List<AlbumPhoto> getAlbumList(String category) {
        LambdaQueryWrapper<AlbumPhoto> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumPhoto::getStatus, 1);

        if (StringUtils.hasText(category) && !"all".equals(category)) {
            wrapper.eq(AlbumPhoto::getCategory, category);
        }

        wrapper.orderByDesc(AlbumPhoto::getPhotoDate);
        wrapper.orderByAsc(AlbumPhoto::getSortOrder);
        return albumPhotoMapper.selectList(wrapper);
    }

    /**
     * 提交在线报名
     */
    public void submitEnrollment(Enrollment enrollment) {
        enrollment.setStatus("PENDING");
        enrollmentMapper.insert(enrollment);
    }
}

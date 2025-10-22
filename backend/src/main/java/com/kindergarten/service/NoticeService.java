package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.entity.Notice;
import com.kindergarten.entity.NoticeRead;
import com.kindergarten.mapper.NoticeMapper;
import com.kindergarten.mapper.NoticeReadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 通知公告服务
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeReadMapper noticeReadMapper;

    /**
     * 获取通知列表（分页）
     */
    public Page<Notice> getNoticeList(Integer pageNum, Integer pageSize) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, "PUBLISHED");
        wrapper.orderByDesc(Notice::getIsTop);
        wrapper.orderByDesc(Notice::getPublishTime);
        return noticeMapper.selectPage(page, wrapper);
    }

    /**
     * 获取通知详情
     */
    public Notice getNoticeDetail(Long id) {
        return noticeMapper.selectById(id);
    }

    /**
     * 标记通知已读
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long noticeId, Long userId) {
        // 检查是否已读
        LambdaQueryWrapper<NoticeRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeRead::getNoticeId, noticeId);
        wrapper.eq(NoticeRead::getUserId, userId);

        if (noticeReadMapper.selectCount(wrapper) == 0) {
            NoticeRead noticeRead = new NoticeRead();
            noticeRead.setNoticeId(noticeId);
            noticeRead.setUserId(userId);
            noticeRead.setReadTime(LocalDateTime.now());
            noticeReadMapper.insert(noticeRead);
        }
    }

    /**
     * 检查是否已读
     */
    public boolean isRead(Long noticeId, Long userId) {
        LambdaQueryWrapper<NoticeRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeRead::getNoticeId, noticeId);
        wrapper.eq(NoticeRead::getUserId, userId);
        return noticeReadMapper.selectCount(wrapper) > 0;
    }

    /**
     * 获取所有通知列表（管理员）
     */
    public Page<Notice> getAllNoticeList(Integer pageNum, Integer pageSize, String status) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Notice::getStatus, status);
        }
        wrapper.orderByDesc(Notice::getCreatedTime);
        return noticeMapper.selectPage(page, wrapper);
    }

    /**
     * 创建通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void createNotice(Notice notice) {
        noticeMapper.insert(notice);
    }

    /**
     * 更新通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(Notice notice) {
        noticeMapper.updateById(notice);
    }

    /**
     * 删除通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        noticeMapper.deleteById(id);
        // 删除关联的已读记录
        LambdaQueryWrapper<NoticeRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeRead::getNoticeId, id);
        noticeReadMapper.delete(wrapper);
    }

    /**
     * 发布通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice != null) {
            notice.setStatus("PUBLISHED");
            notice.setPublishTime(LocalDateTime.now());
            noticeMapper.updateById(notice);
        }
    }
}

package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.entity.Feedback;
import com.kindergarten.mapper.FeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 意见反馈服务
 */
@Service
public class FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    /**
     * 获取反馈列表（分页）
     */
    public Page<Feedback> getFeedbackList(Integer pageNum, Integer pageSize, String status) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();

        if (status != null && !status.isEmpty()) {
            wrapper.eq(Feedback::getStatus, status);
        }

        wrapper.orderByDesc(Feedback::getCreatedTime);
        return feedbackMapper.selectPage(page, wrapper);
    }

    /**
     * 获取用户的反馈列表
     */
    public Page<Feedback> getUserFeedbackList(Long userId, Integer pageNum, Integer pageSize) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId)
               .orderByDesc(Feedback::getCreatedTime);
        return feedbackMapper.selectPage(page, wrapper);
    }

    /**
     * 提交反馈
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitFeedback(Long userId, String content) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setContent(content);
        feedback.setStatus("pending"); // pending-待处理, replied-已回复
        feedbackMapper.insert(feedback);
    }

    /**
     * 回复反馈
     */
    @Transactional(rollbackFor = Exception.class)
    public void replyFeedback(Long feedbackId, Long replierId, String reply) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new RuntimeException("反馈不存在");
        }

        feedback.setReply(reply);
        feedback.setReplierId(replierId);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus("replied");
        feedbackMapper.updateById(feedback);
    }

    /**
     * 删除反馈
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFeedback(Long feedbackId) {
        feedbackMapper.deleteById(feedbackId);
    }

    /**
     * 获取反馈详情
     */
    public Feedback getFeedbackById(Long feedbackId) {
        return feedbackMapper.selectById(feedbackId);
    }
}

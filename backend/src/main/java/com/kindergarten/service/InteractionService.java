package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.entity.Interaction;
import com.kindergarten.mapper.InteractionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 家园互动服务
 */
@Service
public class InteractionService {

    @Autowired
    private InteractionMapper interactionMapper;

    /**
     * 获取互动列表（分页）
     */
    public Page<Interaction> getInteractionList(Integer pageNum, Integer pageSize,
                                                 Long parentId, Long teacherId, Long childId) {
        Page<Interaction> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Interaction> wrapper = new LambdaQueryWrapper<>();

        if (parentId != null) {
            wrapper.eq(Interaction::getParentId, parentId);
        }
        if (teacherId != null) {
            wrapper.eq(Interaction::getTeacherId, teacherId);
        }
        if (childId != null) {
            wrapper.eq(Interaction::getChildId, childId);
        }

        wrapper.orderByDesc(Interaction::getCreatedTime);
        return interactionMapper.selectPage(page, wrapper);
    }

    /**
     * 家长发起互动
     */
    @Transactional(rollbackFor = Exception.class)
    public void createInteraction(Long parentId, Long teacherId, Long childId, String message) {
        Interaction interaction = new Interaction();
        interaction.setParentId(parentId);
        interaction.setTeacherId(teacherId);
        interaction.setChildId(childId);
        interaction.setMessage(message);
        interaction.setIsRead(0); // 0-未读, 1-已读
        interactionMapper.insert(interaction);
    }

    /**
     * 教师回复互动
     */
    @Transactional(rollbackFor = Exception.class)
    public void replyInteraction(Long interactionId, String reply) {
        Interaction interaction = interactionMapper.selectById(interactionId);
        if (interaction == null) {
            throw new RuntimeException("互动记录不存在");
        }

        interaction.setReply(reply);
        interaction.setReplyTime(LocalDateTime.now());
        interaction.setIsRead(1);
        interactionMapper.updateById(interaction);
    }

    /**
     * 标记为已读
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long interactionId) {
        Interaction interaction = interactionMapper.selectById(interactionId);
        if (interaction == null) {
            throw new RuntimeException("互动记录不存在");
        }

        interaction.setIsRead(1);
        interactionMapper.updateById(interaction);
    }

    /**
     * 删除互动记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteInteraction(Long interactionId) {
        interactionMapper.deleteById(interactionId);
    }

    /**
     * 获取互动详情
     */
    public Interaction getInteractionById(Long interactionId) {
        return interactionMapper.selectById(interactionId);
    }

    /**
     * 获取未读互动数量
     */
    public Long getUnreadCount(Long teacherId) {
        LambdaQueryWrapper<Interaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interaction::getTeacherId, teacherId)
               .eq(Interaction::getIsRead, 0);
        return interactionMapper.selectCount(wrapper);
    }
}

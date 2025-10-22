package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kindergarten.entity.*;
import com.kindergarten.mapper.*;
import com.kindergarten.vo.ChildDetailVO;
import com.kindergarten.vo.HomeDataVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 家长端服务
 */
@Service
public class ParentService {

    @Autowired
    private ParentInfoMapper parentInfoMapper;

    @Autowired
    private ParentChildRelationMapper relationMapper;

    @Autowired
    private ChildInfoMapper childInfoMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 获取家长端首页数据
     */
    public HomeDataVO getHomeData(Long userId) {
        HomeDataVO vo = new HomeDataVO();

        // 获取家长信息
        LambdaQueryWrapper<ParentInfo> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(ParentInfo::getUserId, userId);
        ParentInfo parentInfo = parentInfoMapper.selectOne(parentWrapper);

        // 如果家长信息不存在，返回空数据（可能是新注册用户）
        if (parentInfo == null) {
            vo.setChildrenCount(0);
            vo.setUnreadNotices(0);
            vo.setPendingLeave(0);
            vo.setRecentNotices(new ArrayList<>());
            return vo;
        }

        // 获取孩子数量
        LambdaQueryWrapper<ParentChildRelation> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(ParentChildRelation::getParentId, parentInfo.getId());
        Long childrenCount = relationMapper.selectCount(relationWrapper);
        vo.setChildrenCount(childrenCount.intValue());

        // 获取未读通知数量
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getUserId, userId);
        messageWrapper.eq(Message::getIsRead, 0);
        Long unreadCount = messageMapper.selectCount(messageWrapper);
        vo.setUnreadNotices(unreadCount.intValue());

        // 获取待审批请假数量
        List<ParentChildRelation> relations = relationMapper.selectList(relationWrapper);
        List<Long> childIds = relations.stream().map(ParentChildRelation::getChildId).collect(Collectors.toList());

        if (!childIds.isEmpty()) {
            LambdaQueryWrapper<LeaveRequest> leaveWrapper = new LambdaQueryWrapper<>();
            leaveWrapper.in(LeaveRequest::getChildId, childIds);
            leaveWrapper.eq(LeaveRequest::getStatus, "PENDING");
            Long pendingLeave = leaveRequestMapper.selectCount(leaveWrapper);
            vo.setPendingLeave(pendingLeave.intValue());
        } else {
            vo.setPendingLeave(0);
        }

        // 获取最新公告
        LambdaQueryWrapper<Notice> noticeWrapper = new LambdaQueryWrapper<>();
        noticeWrapper.eq(Notice::getStatus, "PUBLISHED");
        noticeWrapper.orderByDesc(Notice::getPublishTime);
        noticeWrapper.last("LIMIT 5");
        List<Notice> notices = noticeMapper.selectList(noticeWrapper);
        vo.setRecentNotices(notices);

        return vo;
    }

    /**
     * 获取我的孩子列表
     */
    public List<ChildDetailVO> getMyChildren(Long userId) {
        // 获取家长信息
        LambdaQueryWrapper<ParentInfo> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(ParentInfo::getUserId, userId);
        ParentInfo parentInfo = parentInfoMapper.selectOne(parentWrapper);

        if (parentInfo == null) {
            return new ArrayList<>();
        }

        // 获取关系
        LambdaQueryWrapper<ParentChildRelation> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(ParentChildRelation::getParentId, parentInfo.getId());
        List<ParentChildRelation> relations = relationMapper.selectList(relationWrapper);

        List<ChildDetailVO> result = new ArrayList<>();
        for (ParentChildRelation relation : relations) {
            ChildInfo child = childInfoMapper.selectById(relation.getChildId());
            if (child != null) {
                ChildDetailVO vo = new ChildDetailVO();
                BeanUtils.copyProperties(child, vo);

                // 获取班级信息
                if (child.getClassId() != null) {
                    ClassInfo classInfo = classInfoMapper.selectById(child.getClassId());
                    if (classInfo != null) {
                        vo.setClassName(classInfo.getClassName());
                        vo.setClassroom(classInfo.getClassroom());
                    }
                }

                vo.setIsPrimary(relation.getIsPrimary());
                result.add(vo);
            }
        }

        return result;
    }

    /**
     * 获取孩子详细信息
     */
    public ChildDetailVO getChildDetail(Long userId, Long childId) {
        // 验证权限
        if (!checkChildPermission(userId, childId)) {
            throw new RuntimeException("无权访问该孩子信息");
        }

        ChildInfo child = childInfoMapper.selectById(childId);
        if (child == null) {
            throw new RuntimeException("孩子信息不存在");
        }

        ChildDetailVO vo = new ChildDetailVO();
        BeanUtils.copyProperties(child, vo);

        // 获取班级信息
        if (child.getClassId() != null) {
            ClassInfo classInfo = classInfoMapper.selectById(child.getClassId());
            if (classInfo != null) {
                vo.setClassName(classInfo.getClassName());
                vo.setClassroom(classInfo.getClassroom());

                // 获取班主任信息
                if (classInfo.getTeacherId() != null) {
                    SysUser teacher = userMapper.selectById(classInfo.getTeacherId());
                    if (teacher != null) {
                        vo.setTeacherName(teacher.getRealName());
                    }
                }
            }
        }

        return vo;
    }

    /**
     * 检查家长是否有权访问该孩子
     */
    private boolean checkChildPermission(Long userId, Long childId) {
        LambdaQueryWrapper<ParentInfo> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(ParentInfo::getUserId, userId);
        ParentInfo parentInfo = parentInfoMapper.selectOne(parentWrapper);

        if (parentInfo == null) {
            return false;
        }

        LambdaQueryWrapper<ParentChildRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentChildRelation::getParentId, parentInfo.getId());
        wrapper.eq(ParentChildRelation::getChildId, childId);

        return relationMapper.selectCount(wrapper) > 0;
    }
}

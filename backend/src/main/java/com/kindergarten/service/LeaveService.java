package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kindergarten.dto.LeaveRequestDTO;
import com.kindergarten.entity.*;
import com.kindergarten.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 请假服务
 */
@Service
public class LeaveService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private ParentInfoMapper parentInfoMapper;

    @Autowired
    private ChildInfoMapper childInfoMapper;

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 提交请假申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitLeaveRequest(Long userId, LeaveRequestDTO dto) {
        // 获取家长信息
        LambdaQueryWrapper<ParentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentInfo::getUserId, userId);
        ParentInfo parentInfo = parentInfoMapper.selectOne(wrapper);

        if (parentInfo == null) {
            throw new RuntimeException("家长信息不存在");
        }

        // 创建请假记录
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setChildId(dto.getChildId());
        leaveRequest.setParentId(parentInfo.getId());
        leaveRequest.setLeaveType(dto.getLeaveType());
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus("PENDING");

        leaveRequestMapper.insert(leaveRequest);

        // 发送消息给家长
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle("请假申请已提交");
        message.setContent("您的请假申请已提交，请等待审批");
        message.setType("SYSTEM");
        message.setIsRead(0);
        messageMapper.insert(message);
    }

    /**
     * 获取请假记录列表
     */
    public List<LeaveRequest> getLeaveList(Long userId) {
        // 获取家长信息
        LambdaQueryWrapper<ParentInfo> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(ParentInfo::getUserId, userId);
        ParentInfo parentInfo = parentInfoMapper.selectOne(parentWrapper);

        if (parentInfo == null) {
            return List.of();
        }

        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaveRequest::getParentId, parentInfo.getId());
        wrapper.orderByDesc(LeaveRequest::getCreatedTime);
        return leaveRequestMapper.selectList(wrapper);
    }

    /**
     * 撤销请假申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelLeaveRequest(Long userId, Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestMapper.selectById(leaveId);

        if (leaveRequest == null) {
            throw new RuntimeException("请假记录不存在");
        }

        // 验证权限
        LambdaQueryWrapper<ParentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentInfo::getUserId, userId);
        ParentInfo parentInfo = parentInfoMapper.selectOne(wrapper);

        if (parentInfo == null || !leaveRequest.getParentId().equals(parentInfo.getId())) {
            throw new RuntimeException("无权操作");
        }

        // 只有待审批的才能撤销
        if (!"PENDING".equals(leaveRequest.getStatus())) {
            throw new RuntimeException("该请假申请不能撤销");
        }

        leaveRequest.setStatus("CANCELLED");
        leaveRequestMapper.updateById(leaveRequest);
    }

    /**
     * 审批请假申请（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public void approveLeaveRequest(Long leaveId, Long approverId, boolean approved, String remark) {
        LeaveRequest leaveRequest = leaveRequestMapper.selectById(leaveId);

        if (leaveRequest == null) {
            throw new RuntimeException("请假记录不存在");
        }

        if (!"PENDING".equals(leaveRequest.getStatus())) {
            throw new RuntimeException("该请假申请已处理");
        }

        leaveRequest.setStatus(approved ? "APPROVED" : "REJECTED");
        leaveRequest.setApproverId(approverId);
        leaveRequest.setApproveTime(LocalDateTime.now());
        leaveRequest.setApproveRemark(remark);

        leaveRequestMapper.updateById(leaveRequest);

        // 通知家长
        ParentInfo parentInfo = parentInfoMapper.selectById(leaveRequest.getParentId());
        if (parentInfo != null) {
            Message message = new Message();
            message.setUserId(parentInfo.getUserId());
            message.setTitle("请假申请" + (approved ? "已通过" : "已拒绝"));
            message.setContent(approved ? "您的请假申请已通过" : "您的请假申请被拒绝，原因：" + remark);
            message.setType("APPROVAL");
            message.setIsRead(0);
            messageMapper.insert(message);
        }
    }
}

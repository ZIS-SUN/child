package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.dto.UserDTO;
import com.kindergarten.entity.*;
import com.kindergarten.mapper.*;
import com.kindergarten.vo.DashboardVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务
 */
@Service
public class AdminService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private ChildInfoMapper childInfoMapper;

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private InteractionMapper interactionMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取数据面板数据
     */
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();

        // 统计幼儿总数
        Long childCount = childInfoMapper.selectCount(new LambdaQueryWrapper<ChildInfo>()
                .eq(ChildInfo::getStatus, 1));
        vo.setTotalChildren(childCount.intValue());

        // 统计班级总数
        Long classCount = classInfoMapper.selectCount(new LambdaQueryWrapper<ClassInfo>()
                .eq(ClassInfo::getStatus, 1));
        vo.setTotalClasses(classCount.intValue());

        // 统计今日出勤率
        LocalDate today = LocalDate.now();
        Long presentCount = attendanceMapper.selectCount(new LambdaQueryWrapper<Attendance>()
                .eq(Attendance::getAttendanceDate, today)
                .eq(Attendance::getStatus, "PRESENT"));

        if (childCount > 0) {
            vo.setAttendanceRate((int) (presentCount * 100.0 / childCount));
        } else {
            vo.setAttendanceRate(0);
        }

        // 待审批请假数量
        Long pendingLeave = leaveRequestMapper.selectCount(new LambdaQueryWrapper<LeaveRequest>()
                .eq(LeaveRequest::getStatus, "PENDING"));
        vo.setPendingLeave(pendingLeave.intValue());

        // 未读留言数量
        Long unreadMessages = interactionMapper.selectCount(new LambdaQueryWrapper<Interaction>()
                .eq(Interaction::getIsRead, 0));
        vo.setUnreadMessages(unreadMessages.intValue());

        return vo;
    }

    /**
     * 获取用户列表
     */
    public Page<SysUser> getUserList(Integer pageNum, Integer pageSize, String role) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (role != null && !role.isEmpty()) {
            wrapper.eq(SysUser::getRole, role);
        }

        wrapper.orderByDesc(SysUser::getCreatedTime);
        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 新增用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDTO dto) {
        // 检查密码是否为空
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        // 检查用户名是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(1);

        userMapper.insert(user);
    }

    /**
     * 更新用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserDTO dto) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());

        userMapper.updateById(user);
    }

    /**
     * 删除用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    /**
     * 启用/禁用用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setStatus(status);
        userMapper.updateById(user);
    }

    /**
     * 重置密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 重置为默认密码: 123456
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.updateById(user);
    }
}

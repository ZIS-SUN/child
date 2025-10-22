package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kindergarten.dto.LoginRequest;
import com.kindergarten.dto.RegisterRequest;
import com.kindergarten.entity.ParentInfo;
import com.kindergarten.entity.SysUser;
import com.kindergarten.mapper.ParentInfoMapper;
import com.kindergarten.mapper.SysUserMapper;
import com.kindergarten.util.JwtUtil;
import com.kindergarten.vo.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private ParentInfoMapper parentInfoMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return new LoginResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getRealName(),
            user.getRole(),
            user.getAvatarUrl()
        );
    }

    /**
     * 家长注册
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, request.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("手机号已被注册");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setRole("PARENT");
        user.setStatus(1);

        userMapper.insert(user);

        // 创建家长信息
        ParentInfo parentInfo = new ParentInfo();
        parentInfo.setUserId(user.getId());
        parentInfo.setRelationship(request.getRelationship());
        parentInfo.setAddress(request.getAddress());
        parentInfo.setEmergencyContact(request.getPhone());

        parentInfoMapper.insert(parentInfo);
    }

    /**
     * 管理员登录
     */
    public LoginResponse adminLogin(LoginRequest request) {
        LoginResponse response = login(request);

        // 验证是否为管理员角色
        if (!"PRINCIPAL".equals(response.getRole()) && !"TEACHER".equals(response.getRole())) {
            throw new RuntimeException("非管理员账号");
        }

        return response;
    }
}

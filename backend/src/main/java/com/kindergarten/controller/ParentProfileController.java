package com.kindergarten.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kindergarten.common.Result;
import com.kindergarten.entity.ParentInfo;
import com.kindergarten.entity.SysUser;
import com.kindergarten.mapper.ParentInfoMapper;
import com.kindergarten.mapper.SysUserMapper;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 家长端-个人信息管理控制器
 */
@Api(tags = "家长端-个人信息管理")
@RestController
@RequestMapping("/parent/profile")
public class ParentProfileController {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private ParentInfoMapper parentInfoMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("获取个人信息")
    @GetMapping
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);

            SysUser user = userMapper.selectById(userId);

            LambdaQueryWrapper<ParentInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ParentInfo::getUserId, userId);
            ParentInfo parentInfo = parentInfoMapper.selectOne(wrapper);

            return Result.success(Map.of(
                "user", user,
                "parentInfo", parentInfo
            ));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("更新个人信息")
    @PutMapping
    public Result<Void> updateProfile(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);

            // 更新用户基本信息
            SysUser user = userMapper.selectById(userId);
            if (request.containsKey("realName")) {
                user.setRealName((String) request.get("realName"));
            }
            if (request.containsKey("phone")) {
                user.setPhone((String) request.get("phone"));
            }
            userMapper.updateById(user);

            // 更新家长信息
            LambdaQueryWrapper<ParentInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ParentInfo::getUserId, userId);
            ParentInfo parentInfo = parentInfoMapper.selectOne(wrapper);

            if (parentInfo != null) {
                if (request.containsKey("relationship")) {
                    parentInfo.setRelationship((String) request.get("relationship"));
                }
                if (request.containsKey("address")) {
                    parentInfo.setAddress((String) request.get("address"));
                }
                if (request.containsKey("emergencyContact")) {
                    parentInfo.setEmergencyContact((String) request.get("emergencyContact"));
                }
                parentInfoMapper.updateById(parentInfo);
            }

            return Result.success("个人信息更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("修改密码")
    @PostMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);

            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            // 验证旧密码
            SysUser user = userMapper.selectById(userId);
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return Result.error("原密码错误");
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userMapper.updateById(user);

            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

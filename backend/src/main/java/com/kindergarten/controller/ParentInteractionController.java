package com.kindergarten.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.Interaction;
import com.kindergarten.entity.ParentInfo;
import com.kindergarten.mapper.ParentInfoMapper;
import com.kindergarten.service.InteractionService;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 家长端-家园互动
 */
@Api(tags = "家长端-家园互动")
@RestController
@RequestMapping("/parent/interaction")
public class ParentInteractionController {

    @Autowired
    private InteractionService interactionService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ParentInfoMapper parentInfoMapper;

    @ApiOperation("获取我的互动列表")
    @GetMapping("")
    public Result<Page<Interaction>> getMyInteractionList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long childId,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        // 获取parent_info的ID
        Long parentInfoId = getParentInfoId(userId);
        if (parentInfoId == null) {
            return Result.error("未找到家长信息");
        }

        Page<Interaction> page = interactionService.getInteractionList(
                pageNum, pageSize, parentInfoId, null, childId);
        return Result.success(page);
    }

    @ApiOperation("发起互动")
    @PostMapping("")
    public Result<Void> createInteraction(
            @RequestParam Long teacherId,
            @RequestParam Long childId,
            @RequestParam String message,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        // 获取parent_info的ID
        Long parentInfoId = getParentInfoId(userId);
        if (parentInfoId == null) {
            return Result.error("未找到家长信息");
        }

        interactionService.createInteraction(parentInfoId, teacherId, childId, message);
        return Result.success("发送成功");
    }

    @ApiOperation("删除互动")
    @DeleteMapping("/{id}")
    public Result<Void> deleteInteraction(@PathVariable Long id) {
        interactionService.deleteInteraction(id);
        return Result.success("删除成功");
    }
    
    /**
     * 通过userId获取parentInfoId
     */
    private Long getParentInfoId(Long userId) {
        LambdaQueryWrapper<ParentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentInfo::getUserId, userId);
        ParentInfo parentInfo = parentInfoMapper.selectOne(wrapper);
        return parentInfo != null ? parentInfo.getId() : null;
    }
}

package com.kindergarten.controller;

import com.kindergarten.common.Result;
import com.kindergarten.entity.Album;
import com.kindergarten.entity.ChildInfo;
import com.kindergarten.entity.ParentChildRelation;
import com.kindergarten.mapper.ChildInfoMapper;
import com.kindergarten.mapper.ParentChildRelationMapper;
import com.kindergarten.service.AlbumService;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 家长端-成长相册
 */
@Api(tags = "家长端-成长相册")
@RestController
@RequestMapping("/parent/albums")
public class ParentAlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ParentChildRelationMapper parentChildRelationMapper;

    @Autowired
    private ChildInfoMapper childInfoMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("获取孩子班级的相册列表")
    @GetMapping("")
    public Result<List<Album>> getMyChildrenAlbums(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 获取家长的所有孩子
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ParentChildRelation> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ParentChildRelation::getParentId, userId);
        List<ParentChildRelation> relations = parentChildRelationMapper.selectList(wrapper);

        List<Album> allAlbums = new ArrayList<>();
        for (ParentChildRelation relation : relations) {
            ChildInfo child = childInfoMapper.selectById(relation.getChildId());
            if (child != null && child.getClassId() != null) {
                List<Album> classAlbums = albumService.getClassAlbums(child.getClassId());
                allAlbums.addAll(classAlbums);
            }
        }

        // 去重（如果有多个孩子在同一个班级）
        List<Album> uniqueAlbums = new ArrayList<>();
        for (Album album : allAlbums) {
            boolean exists = false;
            for (Album unique : uniqueAlbums) {
                if (unique.getId().equals(album.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                uniqueAlbums.add(album);
            }
        }

        return Result.success(uniqueAlbums);
    }

    @ApiOperation("获取相册详情")
    @GetMapping("/{id}")
    public Result<Album> getAlbumDetail(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Album album = albumService.getAlbumById(id);
        if (album == null) {
            return Result.error("相册不存在");
        }

        // 验证家长权限（判断是否有孩子在这个班级）
        if (!hasAccessToClass(userId, album.getClassId())) {
            return Result.error("无权查看该相册");
        }

        return Result.success(album);
    }

    @ApiOperation("获取相册照片列表")
    @GetMapping("/{id}/photos")
    public Result<List<String>> getAlbumPhotos(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Album album = albumService.getAlbumById(id);
        if (album == null) {
            return Result.error("相册不存在");
        }

        if (!hasAccessToClass(userId, album.getClassId())) {
            return Result.error("无权查看该相册");
        }

        List<String> photos = albumService.getAlbumPhotos(id);
        return Result.success(photos);
    }

    /**
     * 验证家长是否有权访问某个班级的内容
     */
    private boolean hasAccessToClass(Long userId, Long classId) {
        // 获取家长的所有孩子
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ParentChildRelation> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ParentChildRelation::getParentId, userId);
        List<ParentChildRelation> relations = parentChildRelationMapper.selectList(wrapper);

        for (ParentChildRelation relation : relations) {
            ChildInfo child = childInfoMapper.selectById(relation.getChildId());
            if (child != null && classId.equals(child.getClassId())) {
                return true;
            }
        }

        return false;
    }
}

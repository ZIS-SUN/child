package com.kindergarten.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.common.Result;
import com.kindergarten.entity.Album;
import com.kindergarten.service.AlbumService;
import com.kindergarten.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理员端-成长相册管理
 */
@Api(tags = "管理员端-成长相册管理")
@RestController
@RequestMapping("/admin/albums")
public class AdminAlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("获取相册列表")
    @GetMapping("")
    public Result<Page<Album>> getAlbumList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String title) {
        Page<Album> page = albumService.getAlbumList(pageNum, pageSize, classId, title);
        return Result.success(page);
    }

    @ApiOperation("获取相册详情")
    @GetMapping("/{id}")
    public Result<Album> getAlbumDetail(@PathVariable Long id) {
        Album album = albumService.getAlbumById(id);
        return Result.success(album);
    }

    @ApiOperation("获取相册照片列表")
    @GetMapping("/{id}/photos")
    public Result<List<String>> getAlbumPhotos(@PathVariable Long id) {
        List<String> photos = albumService.getAlbumPhotos(id);
        return Result.success(photos);
    }

    @ApiOperation("创建相册")
    @PostMapping("")
    public Result<Void> createAlbum(@RequestParam String title,
                                     @RequestParam(required = false) String description,
                                     @RequestParam Long classId,
                                     @RequestParam List<String> imageUrls,
                                     HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long publisherId = jwtUtil.getUserIdFromToken(token);

        albumService.createAlbum(title, description, classId, imageUrls, publisherId);
        return Result.success("相册创建成功");
    }

    @ApiOperation("更新相册")
    @PutMapping("/{id}")
    public Result<Void> updateAlbum(@PathVariable Long id,
                                     @RequestParam(required = false) String title,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false) List<String> imageUrls) {
        albumService.updateAlbum(id, title, description, imageUrls);
        return Result.success("相册更新成功");
    }

    @ApiOperation("删除相册")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return Result.success("相册删除成功");
    }

    @ApiOperation("添加照片到相册")
    @PostMapping("/{id}/photos")
    public Result<Void> addPhotos(@PathVariable Long id,
                                   @RequestParam List<String> imageUrls) {
        albumService.addPhotos(id, imageUrls);
        return Result.success("照片添加成功");
    }

    @ApiOperation("从相册删除照片")
    @DeleteMapping("/{id}/photos")
    public Result<Void> removePhoto(@PathVariable Long id,
                                     @RequestParam String imageUrl) {
        albumService.removePhoto(id, imageUrl);
        return Result.success("照片删除成功");
    }
}

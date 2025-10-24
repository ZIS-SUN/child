package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.entity.Album;
import com.kindergarten.mapper.AlbumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 成长相册服务
 */
@Service
public class AlbumService {

    @Autowired
    private AlbumMapper albumMapper;

    /**
     * 获取相册列表（分页）
     */
    public Page<Album> getAlbumList(Integer pageNum, Integer pageSize, Long classId, String title) {
        Page<Album> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Album> wrapper = new LambdaQueryWrapper<>();

        if (classId != null) {
            wrapper.eq(Album::getClassId, classId);
        }

        if (title != null && !title.isEmpty()) {
            wrapper.like(Album::getTitle, title);
        }

        wrapper.orderByDesc(Album::getPublishTime);
        return albumMapper.selectPage(page, wrapper);
    }

    /**
     * 获取班级相册列表
     */
    public List<Album> getClassAlbums(Long classId) {
        LambdaQueryWrapper<Album> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Album::getClassId, classId)
               .orderByDesc(Album::getPublishTime);
        return albumMapper.selectList(wrapper);
    }

    /**
     * 获取相册详情
     */
    public Album getAlbumById(Long id) {
        return albumMapper.selectById(id);
    }

    /**
     * 创建相册
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAlbum(String title, String description, Long classId,
                           List<String> imageUrls, Long publisherId) {
        Album album = new Album();
        album.setTitle(title);
        album.setDescription(description);
        album.setClassId(classId);
        album.setImageUrls(String.join(",", imageUrls));
        album.setPublisherId(publisherId);
        album.setPublishTime(LocalDateTime.now());

        albumMapper.insert(album);
    }

    /**
     * 更新相册
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAlbum(Long id, String title, String description, List<String> imageUrls) {
        Album album = albumMapper.selectById(id);
        if (album == null) {
            throw new RuntimeException("相册不存在");
        }

        if (title != null) {
            album.setTitle(title);
        }
        if (description != null) {
            album.setDescription(description);
        }
        if (imageUrls != null && !imageUrls.isEmpty()) {
            album.setImageUrls(String.join(",", imageUrls));
        }

        albumMapper.updateById(album);
    }

    /**
     * 删除相册
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(Long id) {
        albumMapper.deleteById(id);
    }

    /**
     * 添加照片到相册
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPhotos(Long albumId, List<String> newImageUrls) {
        Album album = albumMapper.selectById(albumId);
        if (album == null) {
            throw new RuntimeException("相册不存在");
        }

        String existingUrls = album.getImageUrls();
        List<String> allUrls = new java.util.ArrayList<>();

        if (existingUrls != null && !existingUrls.isEmpty()) {
            allUrls.addAll(Arrays.asList(existingUrls.split(",")));
        }
        allUrls.addAll(newImageUrls);

        album.setImageUrls(String.join(",", allUrls));
        albumMapper.updateById(album);
    }

    /**
     * 从相册删除照片
     */
    @Transactional(rollbackFor = Exception.class)
    public void removePhoto(Long albumId, String imageUrl) {
        Album album = albumMapper.selectById(albumId);
        if (album == null) {
            throw new RuntimeException("相册不存在");
        }

        String existingUrls = album.getImageUrls();
        if (existingUrls == null || existingUrls.isEmpty()) {
            return;
        }

        List<String> urls = new java.util.ArrayList<>(Arrays.asList(existingUrls.split(",")));
        urls.remove(imageUrl);

        album.setImageUrls(urls.isEmpty() ? "" : String.join(",", urls));
        albumMapper.updateById(album);
    }

    /**
     * 获取相册中的照片列表
     */
    public List<String> getAlbumPhotos(Long albumId) {
        Album album = albumMapper.selectById(albumId);
        if (album == null || album.getImageUrls() == null || album.getImageUrls().isEmpty()) {
            return new java.util.ArrayList<>();
        }

        return Arrays.asList(album.getImageUrls().split(","));
    }
}

package com.kindergarten.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kindergarten.entity.AlbumPhoto;
import com.kindergarten.mapper.AlbumPhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 相册照片服务（公开照片展示）
 */
@Service
public class AlbumPhotoService {

    @Autowired
    private AlbumPhotoMapper albumPhotoMapper;

    /**
     * 获取照片列表（分页）
     */
    public Page<AlbumPhoto> getPhotoList(Integer pageNum, Integer pageSize, String category, Integer status) {
        Page<AlbumPhoto> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AlbumPhoto> wrapper = new LambdaQueryWrapper<>();

        if (category != null && !category.isEmpty()) {
            wrapper.eq(AlbumPhoto::getCategory, category);
        }

        if (status != null) {
            wrapper.eq(AlbumPhoto::getStatus, status);
        }

        wrapper.orderByDesc(AlbumPhoto::getPhotoDate, AlbumPhoto::getSortOrder);
        return albumPhotoMapper.selectPage(page, wrapper);
    }

    /**
     * 根据分类获取照片
     */
    public List<AlbumPhoto> getPhotosByCategory(String category) {
        LambdaQueryWrapper<AlbumPhoto> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlbumPhoto::getCategory, category)
               .eq(AlbumPhoto::getStatus, 1)
               .orderByAsc(AlbumPhoto::getSortOrder)
               .orderByDesc(AlbumPhoto::getPhotoDate);
        return albumPhotoMapper.selectList(wrapper);
    }

    /**
     * 添加照片
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPhoto(String title, String category, String imageUrl,
                        String description, LocalDateTime photoDate, Integer sortOrder) {
        AlbumPhoto photo = new AlbumPhoto();
        photo.setTitle(title);
        photo.setCategory(category);
        photo.setImageUrl(imageUrl);
        photo.setDescription(description);
        photo.setPhotoDate(photoDate != null ? photoDate : LocalDateTime.now());
        photo.setSortOrder(sortOrder != null ? sortOrder : 0);
        photo.setStatus(1);

        albumPhotoMapper.insert(photo);
    }

    /**
     * 批量添加照片
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchAddPhotos(List<AlbumPhoto> photos) {
        for (AlbumPhoto photo : photos) {
            if (photo.getPhotoDate() == null) {
                photo.setPhotoDate(LocalDateTime.now());
            }
            if (photo.getSortOrder() == null) {
                photo.setSortOrder(0);
            }
            if (photo.getStatus() == null) {
                photo.setStatus(1);
            }
            albumPhotoMapper.insert(photo);
        }
    }

    /**
     * 更新照片信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePhoto(Long id, String title, String description,
                           String category, Integer sortOrder, Integer status) {
        AlbumPhoto photo = albumPhotoMapper.selectById(id);
        if (photo == null) {
            throw new RuntimeException("照片不存在");
        }

        if (title != null) {
            photo.setTitle(title);
        }
        if (description != null) {
            photo.setDescription(description);
        }
        if (category != null) {
            photo.setCategory(category);
        }
        if (sortOrder != null) {
            photo.setSortOrder(sortOrder);
        }
        if (status != null) {
            photo.setStatus(status);
        }

        albumPhotoMapper.updateById(photo);
    }

    /**
     * 删除照片
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePhoto(Long id) {
        albumPhotoMapper.deleteById(id);
    }

    /**
     * 批量删除照片
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePhotos(List<Long> ids) {
        albumPhotoMapper.deleteBatchIds(ids);
    }

    /**
     * 更新照片状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePhotoStatus(Long id, Integer status) {
        AlbumPhoto photo = albumPhotoMapper.selectById(id);
        if (photo == null) {
            throw new RuntimeException("照片不存在");
        }
        photo.setStatus(status);
        albumPhotoMapper.updateById(photo);
    }
}

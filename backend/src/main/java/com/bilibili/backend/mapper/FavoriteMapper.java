package com.bilibili.backend.mapper;

import com.bilibili.backend.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FavoriteMapper {
    int insert(Favorite favorite);
    
    int delete(@Param("userId") Long userId, @Param("videoId") Long videoId);
    
    List<Favorite> findByUserId(@Param("userId") Long userId);
    
    int exists(@Param("userId") Long userId, @Param("videoId") Long videoId);
} 
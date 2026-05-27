package com.project.platform.mapper;

import com.project.platform.entity.MovieRegion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface MovieRegionMapper {
    List<MovieRegion> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie_region WHERE id = #{id}")
    MovieRegion selectById(Integer id);

    @Select("SELECT * FROM movie_region")
    List<MovieRegion> list();

    int insert(MovieRegion entity);

    int updateById(MovieRegion entity);

    boolean removeByIds(List<Integer> ids);

}
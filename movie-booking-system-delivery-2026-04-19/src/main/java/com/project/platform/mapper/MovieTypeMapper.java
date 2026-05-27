package com.project.platform.mapper;

import com.project.platform.entity.MovieType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface MovieTypeMapper {
    List<MovieType> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie_type WHERE id = #{id}")
    MovieType selectById(Integer id);

    @Select("SELECT * FROM movie_type")
    List<MovieType> list();

    int insert(MovieType entity);

    int updateById(MovieType entity);

    boolean removeByIds(List<Integer> ids);

}
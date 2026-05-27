package com.project.platform.mapper;

import com.project.platform.entity.MovieYears;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface MovieYearsMapper {
    List<MovieYears> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie_years WHERE id = #{id}")
    MovieYears selectById(Integer id);

    @Select("SELECT * FROM movie_years")
    List<MovieYears> list();

    int insert(MovieYears entity);

    int updateById(MovieYears entity);

    boolean removeByIds(List<Integer> ids);

}
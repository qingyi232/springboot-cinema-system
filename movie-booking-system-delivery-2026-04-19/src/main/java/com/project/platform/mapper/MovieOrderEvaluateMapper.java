package com.project.platform.mapper;

import com.project.platform.entity.MovieOrderEvaluate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface MovieOrderEvaluateMapper {
    List<MovieOrderEvaluate> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie_order_evaluate WHERE id = #{id}")
    MovieOrderEvaluate selectById(Integer id);

    @Select("SELECT * FROM movie_order_evaluate")
    List<MovieOrderEvaluate> list();

    int insert(MovieOrderEvaluate entity);

    int updateById(MovieOrderEvaluate entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT AVG(rate) FROM movie_order_evaluate WHERE movie_id = #{id}")
    Float selectAvg(Integer id);

}
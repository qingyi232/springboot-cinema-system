package com.project.platform.mapper;

import com.project.platform.entity.MovieCollect;
import com.project.platform.vo.ValueNameVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface MovieCollectMapper {
    List<MovieCollect> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie_collect WHERE id = #{id}")
    MovieCollect selectById(Integer id);

    @Select("SELECT * FROM movie_collect")
    List<MovieCollect> list();

    int insert(MovieCollect entity);

    int updateById(MovieCollect entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT * FROM movie_collect WHERE movie_id = #{movieId} and user_id= #{userId}")
    MovieCollect selectByMovieIdAndUserId(Integer movieId, Integer userId);

    @Select("select movie.movie_type_id   as name,count(movie.movie_type_id)  as value from movie_collect" +
            "    left join movie on movie_collect.movie_id=movie.id" +
            "    where  movie_collect.user_id= #{userId}" +
            "    group by movie.movie_type_id")
    List<ValueNameVO> statisticsMovieTypeIdByUserId(Integer userId);

}
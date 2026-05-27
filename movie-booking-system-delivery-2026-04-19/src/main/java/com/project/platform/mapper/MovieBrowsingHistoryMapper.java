package com.project.platform.mapper;

import com.project.platform.entity.MovieBrowsingHistory;
import com.project.platform.entity.MovieCollect;
import com.project.platform.vo.ValueNameVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface MovieBrowsingHistoryMapper {
    List<MovieBrowsingHistory> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie_browsing_history WHERE id = #{id}")
    MovieBrowsingHistory selectById(Integer id);

    @Select("SELECT * FROM movie_browsing_history")
    List<MovieBrowsingHistory> list();

    int insert(MovieBrowsingHistory entity);

    int updateById(MovieBrowsingHistory entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT * FROM movie_browsing_history WHERE movie_id = #{movieId} and user_id= #{userId}")
    MovieBrowsingHistory selectByMovieIdAndUserId(Integer movieId, Integer userId);

    @Select("select movie.movie_type_id  as name,count(movie.movie_type_id)  as value from movie_browsing_history" +
            "    left join movie on movie_browsing_history.movie_id=movie.id" +
            "    where  movie_browsing_history.user_id= #{userId}" +
            "    group by movie.movie_type_id")
    List<ValueNameVO> statisticsMovieTypeIdByUserId(Integer userId);
}
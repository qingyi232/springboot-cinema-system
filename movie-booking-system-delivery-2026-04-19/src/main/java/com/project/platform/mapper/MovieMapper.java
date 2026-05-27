package com.project.platform.mapper;

import com.project.platform.entity.Movie;
import com.project.platform.vo.ValueNameVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface MovieMapper {
    List<Movie> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie WHERE id = #{id}")
    Movie selectById(Integer id);

    @Select("SELECT * FROM movie")
    List<Movie> list();

    int insert(Movie entity);

    int updateById(Movie entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT * FROM movie order by box_office desc limit #{size}")
    List<Movie> boxOfficeTop(int size);

    @Select("select movie_type.name  as name,movie.count  as value from  movie_type  join ( SELECT movie_type_id,COUNT(*) AS count FROM movie  GROUP BY movie_type_id)  movie on movie.movie_type_id=movie_type.id")
    List<ValueNameVO> selectTypeCount();

    @Select("select movie_years.name  as name,movie.count  as value from  movie_years  join ( SELECT movie_years_id,COUNT(*) AS count FROM movie  GROUP BY movie_years_id)  movie on movie.movie_years_id=movie_years.id")
    List<ValueNameVO> selectYearsCount();

    @Select("select movie_region.name  as name,movie.count  as value from  movie_region  join ( SELECT movie_region_id,COUNT(*) AS count FROM movie  GROUP BY movie_region_id)  movie on movie.movie_region_id=movie_region.id")
    List<ValueNameVO> selectRegionCount();

}
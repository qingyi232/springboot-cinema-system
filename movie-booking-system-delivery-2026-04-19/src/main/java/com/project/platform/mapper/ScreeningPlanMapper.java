package com.project.platform.mapper;

import cn.hutool.json.JSONObject;
import com.project.platform.entity.ScreeningPlan;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface ScreeningPlanMapper {
    List<ScreeningPlan> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM screening_plan WHERE id = #{id}")
    ScreeningPlan selectById(Integer id);

    @Select("SELECT * FROM screening_plan")
    List<ScreeningPlan> list();

    int insert(ScreeningPlan entity);

    int updateById(ScreeningPlan entity);

    boolean removeByIds(List<Integer> ids);


    @Select("""
            select cinema_id,
                   ANY_VALUE(nickname)  as nickname,
                   ANY_VALUE(label)  as label,
                   ANY_VALUE(address)  as address,
                   MIN(price) as price
            from (select cinema.nickname,
                         cinema.label,
                         cinema.address,
                         screening_plan.*
                  from screening_plan
                           join cinema on screening_plan.cinema_id = cinema.id
                  where
                      movie_id=  #{movieId} and
                      screening_plan.start_time >= NOW()) a
            group by cinema_id
            order by MIN(start_time) asc
            """)
    List<JSONObject> selectAvailableCinema(@Param("movieId") Integer movieId);


    @Select("""
            select movie_room.name,
                   movie_room.label,
                   screening_plan.*
            from screening_plan
                     join movie_room on screening_plan.movie_room_id = movie_room.id
            where
                screening_plan.cinema_id=  #{cinemaId} 
                and screening_plan.movie_id=  #{movieId}   
                and screening_plan.start_time >= NOW()
            order by screening_plan.start_time asc
            """)
    List<JSONObject> selectAvailableMovieRoom(@Param("cinemaId") Integer cinemaId, @Param("movieId") Integer movieId);


}

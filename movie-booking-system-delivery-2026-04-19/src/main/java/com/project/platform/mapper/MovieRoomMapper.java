package com.project.platform.mapper;

import com.project.platform.entity.MovieRoom;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface MovieRoomMapper {
    List<MovieRoom> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie_room WHERE id = #{id}")
    MovieRoom selectById(Integer id);

    @Select("SELECT * FROM movie_room")
    List<MovieRoom> list();

    @Select("SELECT * FROM movie_room WHERE cinema_id = #{cinemaId}")
    List<MovieRoom> listByCinemaId(Integer cinemaId);

    int insert(MovieRoom entity);

    int updateById(MovieRoom entity);

    boolean removeByIds(List<Integer> ids);

}
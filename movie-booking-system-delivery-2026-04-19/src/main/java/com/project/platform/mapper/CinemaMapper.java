package com.project.platform.mapper;

import com.project.platform.entity.Admin;
import com.project.platform.entity.Cinema;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface CinemaMapper {
    List<Cinema> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM cinema WHERE id = #{id}")
    Cinema selectById(Integer id);

    @Select("SELECT * FROM cinema")
    List<Cinema> list();

    int insert(Cinema entity);

    int updateById(Cinema entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT * FROM cinema WHERE username = #{username}")
    Cinema selectByUserName(String username);


    @Select("SELECT * FROM cinema WHERE tel = #{tel}")
    Cinema selectByTel(String tel);

}
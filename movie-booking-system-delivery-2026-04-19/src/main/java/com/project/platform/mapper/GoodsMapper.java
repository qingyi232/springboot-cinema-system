package com.project.platform.mapper;

import com.project.platform.entity.Goods;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface GoodsMapper {
    List<Goods> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM goods WHERE id = #{id}")
    Goods selectById(Integer id);

    @Select("SELECT * FROM goods ORDER BY id DESC")
    List<Goods> list();

    int insert(Goods entity);

    int updateById(Goods entity);

    boolean removeByIds(List<Integer> ids);
}

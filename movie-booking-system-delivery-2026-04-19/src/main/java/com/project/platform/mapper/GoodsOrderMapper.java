package com.project.platform.mapper;

import com.project.platform.entity.GoodsOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface GoodsOrderMapper {
    List<GoodsOrder> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM goods_order WHERE id = #{id}")
    GoodsOrder selectById(Integer id);

    @Select("SELECT * FROM goods_order ORDER BY id DESC")
    List<GoodsOrder> list();

    int insert(GoodsOrder entity);

    int updateById(GoodsOrder entity);

    boolean removeByIds(List<Integer> ids);
}

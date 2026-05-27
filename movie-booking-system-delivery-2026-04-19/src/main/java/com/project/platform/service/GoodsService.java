package com.project.platform.service;

import com.project.platform.entity.Goods;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface GoodsService {
    PageVO<Goods> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    Goods selectById(Integer id);

    List<Goods> list();

    void insert(Goods entity);

    void updateById(Goods entity);

    void removeByIds(List<Integer> ids);
}

package com.project.platform.service;

import com.project.platform.entity.GoodsOrder;
import com.project.platform.vo.GoodsOrderSubmitRequest;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface GoodsOrderService {
    PageVO<GoodsOrder> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    GoodsOrder selectById(Integer id);

    List<GoodsOrder> list();

    void submit(GoodsOrderSubmitRequest request);

    void evaluate(Integer id, GoodsOrder entity);

    void removeByIds(List<Integer> ids);
}

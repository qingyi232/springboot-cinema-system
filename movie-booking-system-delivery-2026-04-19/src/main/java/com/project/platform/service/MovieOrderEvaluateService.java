package com.project.platform.service;

import com.project.platform.entity.MovieOrderEvaluate;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 订单评价
 */
public interface MovieOrderEvaluateService {

    PageVO<MovieOrderEvaluate> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieOrderEvaluate selectById(Integer id);

    List<MovieOrderEvaluate> list();

    void insert(MovieOrderEvaluate entity);

    void updateById(MovieOrderEvaluate entity);

    void removeByIds(List<Integer> id);
}

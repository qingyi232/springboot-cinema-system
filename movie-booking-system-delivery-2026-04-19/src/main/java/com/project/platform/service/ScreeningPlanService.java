package com.project.platform.service;

import com.project.platform.entity.ScreeningPlan;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 放映计划
 */
public interface ScreeningPlanService {

    PageVO<ScreeningPlan> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    ScreeningPlan selectById(Integer id);

    List<ScreeningPlan> list();

    void insert(ScreeningPlan entity);

    void updateById(ScreeningPlan entity);

    void removeByIds(List<Integer> id);
}

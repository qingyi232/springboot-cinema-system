package com.project.platform.service;

import com.project.platform.entity.MovieYears;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影年代
 */
public interface MovieYearsService {

    PageVO<MovieYears> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieYears selectById(Integer id);

    List<MovieYears> list();

    void insert(MovieYears entity);

    void updateById(MovieYears entity);

    void removeByIds(List<Integer> id);
}

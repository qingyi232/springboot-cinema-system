package com.project.platform.service;

import com.project.platform.entity.MovieCollect;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影收藏
 */
public interface MovieCollectService {

    PageVO<MovieCollect> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieCollect selectById(Integer id);

    List<MovieCollect> list();

    void insert(MovieCollect entity);

    void updateById(MovieCollect entity);

    void removeByIds(List<Integer> id);
}

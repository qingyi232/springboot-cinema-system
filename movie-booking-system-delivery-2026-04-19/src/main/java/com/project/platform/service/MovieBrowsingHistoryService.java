package com.project.platform.service;

import com.project.platform.entity.MovieBrowsingHistory;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影浏览历史
 */
public interface MovieBrowsingHistoryService {

    PageVO<MovieBrowsingHistory> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieBrowsingHistory selectById(Integer id);

    List<MovieBrowsingHistory> list();

    void insert(MovieBrowsingHistory entity);

    void updateById(MovieBrowsingHistory entity);

    void removeByIds(List<Integer> id);
}

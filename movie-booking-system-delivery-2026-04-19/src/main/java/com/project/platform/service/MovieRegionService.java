package com.project.platform.service;

import com.project.platform.entity.MovieRegion;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影区域
 */
public interface MovieRegionService {

    PageVO<MovieRegion> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieRegion selectById(Integer id);

    List<MovieRegion> list();

    void insert(MovieRegion entity);

    void updateById(MovieRegion entity);

    void removeByIds(List<Integer> id);
}

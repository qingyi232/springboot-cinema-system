package com.project.platform.service;

import com.project.platform.entity.Movie;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影信息
 */
public interface MovieService {

    PageVO<Movie> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    Movie selectById(Integer id);

    List<Movie> list();

    void insert(Movie entity);

    void updateById(Movie entity);

    void removeByIds(List<Integer> id);

    List<Movie> boxOfficeTop(int size);
    List<Movie> recommend(Integer size);


}

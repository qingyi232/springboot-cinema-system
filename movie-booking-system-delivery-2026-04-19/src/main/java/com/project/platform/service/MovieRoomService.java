package com.project.platform.service;

import com.project.platform.entity.MovieRoom;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影影厅
 */
public interface MovieRoomService {

    PageVO<MovieRoom> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieRoom selectById(Integer id);

    List<MovieRoom> list();

    void insert(MovieRoom entity);

    void updateById(MovieRoom entity);

    void removeByIds(List<Integer> id);
}

package com.project.platform.service;

import com.project.platform.entity.Cinema;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影院
 */
public interface CinemaService extends CommonService {

    PageVO<Cinema> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    Cinema selectById(Integer id);

    List<Cinema> list();

    void insert(Cinema entity);

    void updateById(Cinema entity);

    void removeByIds(List<Integer> id);

    Cinema selectByTel(String tel);
}

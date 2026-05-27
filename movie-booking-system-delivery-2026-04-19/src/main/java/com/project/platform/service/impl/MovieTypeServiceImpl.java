package com.project.platform.service.impl;

import com.project.platform.entity.MovieType;
import com.project.platform.mapper.MovieTypeMapper;
import com.project.platform.service.MovieTypeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影分类
 */
@Service
public class MovieTypeServiceImpl  implements MovieTypeService {
    @Resource
    private MovieTypeMapper movieTypeMapper;
    
    @Override
    public PageVO<MovieType> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieType> page = new PageVO();
        List<MovieType> list = movieTypeMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieTypeMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieType selectById(Integer id) {
        MovieType movieType = movieTypeMapper.selectById(id);
        return movieType;
    }

    @Override
    public List<MovieType> list() {
        return movieTypeMapper.list();
    }
    @Override
    public void insert(MovieType entity) {
        check(entity);
        movieTypeMapper.insert(entity);
    }
    @Override
    public void updateById(MovieType entity) {
        check(entity);
        movieTypeMapper.updateById(entity);
    }
    private void check(MovieType entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        movieTypeMapper.removeByIds(ids);
    }
}

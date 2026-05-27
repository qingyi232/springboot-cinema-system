package com.project.platform.service.impl;

import com.project.platform.entity.MovieYears;
import com.project.platform.mapper.MovieYearsMapper;
import com.project.platform.service.MovieYearsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影年代
 */
@Service
public class MovieYearsServiceImpl  implements MovieYearsService {
    @Resource
    private MovieYearsMapper movieYearsMapper;
    
    @Override
    public PageVO<MovieYears> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieYears> page = new PageVO();
        List<MovieYears> list = movieYearsMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieYearsMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieYears selectById(Integer id) {
        MovieYears movieYears = movieYearsMapper.selectById(id);
        return movieYears;
    }

    @Override
    public List<MovieYears> list() {
        return movieYearsMapper.list();
    }
    @Override
    public void insert(MovieYears entity) {
        check(entity);
        movieYearsMapper.insert(entity);
    }
    @Override
    public void updateById(MovieYears entity) {
        check(entity);
        movieYearsMapper.updateById(entity);
    }
    private void check(MovieYears entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        movieYearsMapper.removeByIds(ids);
    }
}

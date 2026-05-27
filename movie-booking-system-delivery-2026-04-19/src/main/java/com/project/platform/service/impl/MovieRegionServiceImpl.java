package com.project.platform.service.impl;

import com.project.platform.entity.MovieRegion;
import com.project.platform.mapper.MovieRegionMapper;
import com.project.platform.service.MovieRegionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影区域
 */
@Service
public class MovieRegionServiceImpl  implements MovieRegionService {
    @Resource
    private MovieRegionMapper movieRegionMapper;
    
    @Override
    public PageVO<MovieRegion> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieRegion> page = new PageVO();
        List<MovieRegion> list = movieRegionMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieRegionMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieRegion selectById(Integer id) {
        MovieRegion movieRegion = movieRegionMapper.selectById(id);
        return movieRegion;
    }

    @Override
    public List<MovieRegion> list() {
        return movieRegionMapper.list();
    }
    @Override
    public void insert(MovieRegion entity) {
        check(entity);
        movieRegionMapper.insert(entity);
    }
    @Override
    public void updateById(MovieRegion entity) {
        check(entity);
        movieRegionMapper.updateById(entity);
    }
    private void check(MovieRegion entity) {

    }
    @Override
    public void removeByIds(List<Integer> ids) {
        movieRegionMapper.removeByIds(ids);
    }
}

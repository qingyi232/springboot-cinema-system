package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.MovieCollect;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieCollectMapper;
import com.project.platform.service.MovieCollectService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 电影收藏
 */
@Service
public class MovieCollectServiceImpl implements MovieCollectService {
    @Resource
    private MovieCollectMapper movieCollectMapper;

    @Override
    public PageVO<MovieCollect> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieCollect> page = new PageVO();
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && "USER".equals(currentUser.getType())) {
            query.put("userId", currentUser.getId());
        }
        List<MovieCollect> list = movieCollectMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieCollectMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieCollect selectById(Integer id) {
        MovieCollect movieCollect = movieCollectMapper.selectById(id);
        return movieCollect;
    }

    @Override
    public List<MovieCollect> list() {
        return movieCollectMapper.list();
    }

    @Override
    public void insert(MovieCollect entity) {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null || !"USER".equals(currentUser.getType())) {
            throw new CustomException("普通用户才允许收藏电影");
        }
        entity.setUserId(currentUser.getId());
        check(entity);
        movieCollectMapper.insert(entity);
    }

    @Override
    public void updateById(MovieCollect entity) {
        ensureCanMaintain(entity == null ? null : entity.getId());
        check(entity);
        movieCollectMapper.updateById(entity);
    }

    private void check(MovieCollect entity) {
        if (entity == null || entity.getMovieId() == null || entity.getUserId() == null) {
            throw new CustomException("想看信息不能为空");
        }
        MovieCollect movieCollect = movieCollectMapper.selectByMovieIdAndUserId(entity.getMovieId(), entity.getUserId());
        if (movieCollect != null && !Objects.equals(movieCollect.getId(), entity.getId())) {
            throw new CustomException("电影已经收藏过了");
        }

    }

    @Override
    public void removeByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        if ("ADMIN".equals(currentUser.getType())) {
            movieCollectMapper.removeByIds(ids);
            return;
        }
        if (!"USER".equals(currentUser.getType())) {
            throw new CustomException("当前账号无权取消想看");
        }
        for (Integer id : ids) {
            ensureCanMaintain(id);
        }
        movieCollectMapper.removeByIds(ids);
    }

    private void ensureCanMaintain(Integer id) {
        if (id == null) {
            return;
        }
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        if ("ADMIN".equals(currentUser.getType())) {
            return;
        }
        if (!"USER".equals(currentUser.getType())) {
            throw new CustomException("当前账号无权维护想看记录");
        }
        MovieCollect collect = movieCollectMapper.selectById(id);
        if (collect != null && !Objects.equals(collect.getUserId(), currentUser.getId())) {
            throw new CustomException("无权操作其他用户的想看记录");
        }
    }
}

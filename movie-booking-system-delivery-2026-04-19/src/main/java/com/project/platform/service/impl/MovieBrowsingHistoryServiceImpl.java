package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.MovieBrowsingHistory;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieBrowsingHistoryMapper;
import com.project.platform.service.MovieBrowsingHistoryService;
import com.project.platform.utils.CurrentUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 电影浏览历史
 */
@Service
public class MovieBrowsingHistoryServiceImpl implements MovieBrowsingHistoryService {
    @Resource
    private MovieBrowsingHistoryMapper movieBrowsingHistoryMapper;

    @Override
    public PageVO<MovieBrowsingHistory> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieBrowsingHistory> page = new PageVO();
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && "USER".equals(currentUser.getType())) {
            query.put("userId", currentUser.getId());
        }
        List<MovieBrowsingHistory> list = movieBrowsingHistoryMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieBrowsingHistoryMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieBrowsingHistory selectById(Integer id) {
        MovieBrowsingHistory movieBrowsingHistory = movieBrowsingHistoryMapper.selectById(id);
        return movieBrowsingHistory;
    }

    @Override
    public List<MovieBrowsingHistory> list() {
        return movieBrowsingHistoryMapper.list();
    }

    @Override
    public void insert(MovieBrowsingHistory entity) {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null || !"USER".equals(currentUser.getType())) {
            // 只记录普通用户的流量历史
            return;
        }
        entity.setUserId(currentUser.getId());
        MovieBrowsingHistory movieBrowsingHistory = movieBrowsingHistoryMapper.selectByMovieIdAndUserId(entity.getMovieId(), entity.getUserId());
        //先删除，再添加，一个电影只能记录一次
        if (movieBrowsingHistory != null) {
            List<Integer> ids = new ArrayList<>();
            ids.add(movieBrowsingHistory.getId());
            removeByIds(ids);
        }
        check(entity);
        movieBrowsingHistoryMapper.insert(entity);
    }

    @Override
    public void updateById(MovieBrowsingHistory entity) {
        ensureCanMaintain(entity == null ? null : entity.getId());
        check(entity);
        movieBrowsingHistoryMapper.updateById(entity);
    }

    private void check(MovieBrowsingHistory entity) {

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
            movieBrowsingHistoryMapper.removeByIds(ids);
            return;
        }
        if (!"USER".equals(currentUser.getType())) {
            throw new CustomException("当前账号无权删除浏览记录");
        }
        for (Integer id : ids) {
            ensureCanMaintain(id);
        }
        movieBrowsingHistoryMapper.removeByIds(ids);
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
            throw new CustomException("当前账号无权维护浏览记录");
        }
        MovieBrowsingHistory history = movieBrowsingHistoryMapper.selectById(id);
        if (history != null && !currentUser.getId().equals(history.getUserId())) {
            throw new CustomException("无权操作其他用户的浏览记录");
        }
    }
}

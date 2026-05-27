package com.project.platform.service.impl;

import com.project.platform.entity.Movie;
import com.project.platform.entity.MovieBrowsingHistory;
import com.project.platform.entity.MovieCollect;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieBrowsingHistoryMapper;
import com.project.platform.mapper.MovieCollectMapper;
import com.project.platform.mapper.MovieMapper;
import com.project.platform.service.MovieService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.ValueNameVO;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import com.project.platform.vo.PageVO;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 电影信息
 */
@Service
public class MovieServiceImpl implements MovieService {
    @Resource
    private MovieMapper movieMapper;

    @Resource
    private MovieBrowsingHistoryMapper movieBrowsingHistoryMapper;
    @Resource
    private MovieCollectMapper movieCollectMapper;
    ;

    @Override
    public PageVO<Movie> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Movie> page = new PageVO();
        Map<String, Object> actualQuery = new HashMap<>(query);
        if (CurrentUserThreadLocal.getCurrentUser() != null && "USER".equals(CurrentUserThreadLocal.getCurrentUser().getType())) {
            actualQuery.put("currentUserId", CurrentUserThreadLocal.getCurrentUser().getId());
        }
        List<Movie> list = movieMapper.queryPage((pageNum - 1) * pageSize, pageSize, actualQuery);
        page.setList(list);
        page.setTotal(movieMapper.queryCount(actualQuery));
        return page;
    }

    @Override
    public Movie selectById(Integer id) {
        Map<String, Object> query = new HashMap<>();
        query.put("id", id);
        if (CurrentUserThreadLocal.getCurrentUser() != null && "USER".equals(CurrentUserThreadLocal.getCurrentUser().getType())) {
            query.put("currentUserId", CurrentUserThreadLocal.getCurrentUser().getId());
        }
        List<Movie> movieList = movieMapper.queryPage(0, 1, query);
        if (movieList == null || movieList.isEmpty()) {
            throw new CustomException("电影不存在");
        }
        return movieList.get(0);
    }

    @Override
    public List<Movie> list() {
        return movieMapper.list();
    }

    @Override
    public void insert(Movie entity) {
        check(entity);
        // entity.setBoxOffice(0);
        movieMapper.insert(entity);
    }

    @Override
    public void updateById(Movie entity) {
        check(entity);
        movieMapper.updateById(entity);
    }

    private void check(Movie entity) {

    }

    @Override
    public void removeByIds(List<Integer> ids) {
        movieMapper.removeByIds(ids);
    }


    @Override
    public List<Movie> boxOfficeTop(int size) {
        return movieMapper.boxOfficeTop(size);
    }

    @Override
    public List<Movie> recommend(Integer size) {
        Map<String, Object> query = new HashMap<>();
        Integer currentUserId = null;
        if (CurrentUserThreadLocal.getCurrentUser() != null) {
            currentUserId = CurrentUserThreadLocal.getCurrentUser().getId();
            if ("USER".equals(CurrentUserThreadLocal.getCurrentUser().getType())) {
                query.put("currentUserId", currentUserId);
            }
        }
        List<Movie> movieList = movieMapper.queryPage(0, 1000, query);
        if (currentUserId == null) {
            return movieList.stream()
                    .sorted(Comparator.comparing(Movie::getBoxOffice, Comparator.nullsLast(Float::compareTo)).reversed())
                    .limit(size)
                    .collect(Collectors.toList());
        }
        //浏览记录
        List<ValueNameVO> movieBrowsingHistoryStatisticsList = movieBrowsingHistoryMapper.statisticsMovieTypeIdByUserId(currentUserId);
        //收藏
        List<ValueNameVO> movieCollectStatisticsList = movieCollectMapper.statisticsMovieTypeIdByUserId(currentUserId);
        for (Movie movie : movieList) {
            for (ValueNameVO item : movieBrowsingHistoryStatisticsList) {

                if ((movie.getMovieTypeId().equals(item.getName()))) {
                    movie.setWeight(movie.getWeight() + 1);
                }
            }
            for (ValueNameVO item : movieCollectStatisticsList) {
                if ((movie.getMovieTypeId().equals(item.getName()))) {
                    movie.setWeight(movie.getWeight() + 1);
                }
            }
        }
        //根据权重排序
        return movieList.stream()
                .sorted(Comparator.comparing(Movie::getWeight).reversed())
                .limit(size)
                .collect(Collectors.toList());
    }


}

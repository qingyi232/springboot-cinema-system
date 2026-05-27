package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Movie;
import com.project.platform.entity.MovieOrder;
import com.project.platform.entity.MovieOrderEvaluate;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieMapper;
import com.project.platform.mapper.MovieOrderEvaluateMapper;
import com.project.platform.mapper.MovieOrderMapper;
import com.project.platform.service.MovieOrderEvaluateService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * 订单评价
 */
@Service
public class MovieOrderEvaluateServiceImpl implements MovieOrderEvaluateService {
    @Resource
    private MovieOrderEvaluateMapper movieOrderEvaluateMapper;

    @Resource
    private MovieOrderMapper movieOrderMapper;

    @Resource
    private MovieMapper movieMapper;

    @Override
    public PageVO<MovieOrderEvaluate> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieOrderEvaluate> page = new PageVO();
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getType(), "USER")) {
            query.put("userId", currentUser.getId());
        }
        List<MovieOrderEvaluate> list = movieOrderEvaluateMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieOrderEvaluateMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieOrderEvaluate selectById(Integer id) {
        return movieOrderEvaluateMapper.selectById(id);
    }

    @Override
    public List<MovieOrderEvaluate> list() {
        return movieOrderEvaluateMapper.list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(MovieOrderEvaluate entity) {
        CurrentUserDTO currentUser = requireCurrentUser();
        if (!Objects.equals(currentUser.getType(), "USER")) {
            throw new CustomException("普通用户才能评价");
        }
        entity.setUserId(currentUser.getId());
        validateForInsert(entity, currentUser.getId());
        movieOrderEvaluateMapper.insert(entity);

        MovieOrder movieOrder = movieOrderMapper.selectById(entity.getMovieOrderId());
        movieOrder.setOrderEvaluateId(entity.getId());
        movieOrderMapper.updateById(movieOrder);
        recalculateMovieScore(entity.getMovieId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(MovieOrderEvaluate entity) {
        if (entity == null || entity.getId() == null) {
            throw new CustomException("评价ID不能为空");
        }
        CurrentUserDTO currentUser = requireCurrentUser();
        MovieOrderEvaluate existing = movieOrderEvaluateMapper.selectById(entity.getId());
        if (existing == null) {
            throw new CustomException("评价不存在");
        }

        entity.setUserId(existing.getUserId());
        entity.setMovieId(existing.getMovieId());
        entity.setMovieOrderId(existing.getMovieOrderId());
        if (entity.getContent() == null) {
            entity.setContent(existing.getContent());
        }
        if (entity.getRate() == null) {
            entity.setRate(existing.getRate());
        }
        validateRateAndContent(entity);

        if (Objects.equals(currentUser.getType(), "USER") && !Objects.equals(existing.getUserId(), currentUser.getId())) {
            throw new CustomException("无权修改该评价");
        }
        if (!Objects.equals(currentUser.getType(), "USER") && !Objects.equals(currentUser.getType(), "ADMIN")) {
            throw new CustomException("当前账号无权修改评价");
        }

        movieOrderEvaluateMapper.updateById(entity);
        recalculateMovieScore(existing.getMovieId());
    }

    private void validateForInsert(MovieOrderEvaluate entity, Integer userId) {
        if (entity == null) {
            throw new CustomException("评价信息不能为空");
        }
        if (entity.getMovieOrderId() == null) {
            throw new CustomException("订单不能为空");
        }
        MovieOrder movieOrder = movieOrderMapper.selectById(entity.getMovieOrderId());
        if (movieOrder == null) {
            throw new CustomException("订单不存在");
        }
        if (!Objects.equals(movieOrder.getUserId(), userId)) {
            throw new CustomException("无权评价该订单");
        }
        if (!Objects.equals(movieOrder.getMovieId(), entity.getMovieId())) {
            throw new CustomException("订单和电影信息不匹配");
        }
        if (!"已完成".equals(movieOrder.getStatus())) {
            throw new CustomException("订单未完成，暂不能评价");
        }
        if (movieOrder.getOrderEvaluateId() != null) {
            throw new CustomException("该订单已评价");
        }
        validateRateAndContent(entity);
    }

    private void validateRateAndContent(MovieOrderEvaluate entity) {
        if (entity.getRate() == null) {
            throw new CustomException("请先填写评分");
        }
        if (entity.getRate() < 1 || entity.getRate() > 5) {
            throw new CustomException("评分范围为1到5分");
        }
        String content = entity.getContent() == null ? "" : entity.getContent().trim();
        if (content.isEmpty()) {
            content = "观影体验不错";
        }
        if (content.length() > 200) {
            throw new CustomException("评价内容不能超过200字");
        }
        entity.setContent(content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        CurrentUserDTO currentUser = requireCurrentUser();
        if (!Objects.equals(currentUser.getType(), "ADMIN")) {
            throw new CustomException("只有管理员可以删除评价");
        }

        Set<Integer> movieIds = new LinkedHashSet<>();
        for (Integer id : ids) {
            MovieOrderEvaluate evaluate = movieOrderEvaluateMapper.selectById(id);
            if (evaluate == null) {
                continue;
            }
            movieIds.add(evaluate.getMovieId());
            MovieOrder movieOrder = movieOrderMapper.selectById(evaluate.getMovieOrderId());
            if (movieOrder != null && Objects.equals(movieOrder.getOrderEvaluateId(), evaluate.getId())) {
                movieOrder.setOrderEvaluateId(null);
                movieOrderMapper.updateById(movieOrder);
            }
        }

        movieOrderEvaluateMapper.removeByIds(ids);

        for (Integer movieId : movieIds) {
            recalculateMovieScore(movieId);
        }
    }

    private void recalculateMovieScore(Integer movieId) {
        if (movieId == null) {
            return;
        }
        Movie movie = movieMapper.selectById(movieId);
        if (movie == null) {
            return;
        }
        Float avg = movieOrderEvaluateMapper.selectAvg(movieId);
        movie.setScore(avg == null ? 0 : Math.round(avg));
        movieMapper.updateById(movie);
    }

    private CurrentUserDTO requireCurrentUser() {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        return currentUser;
    }
}

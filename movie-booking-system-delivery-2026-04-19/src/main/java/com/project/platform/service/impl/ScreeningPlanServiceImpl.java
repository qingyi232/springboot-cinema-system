package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Movie;
import com.project.platform.entity.MovieRoom;
import com.project.platform.entity.ScreeningPlan;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieMapper;
import com.project.platform.mapper.MovieRoomMapper;
import com.project.platform.mapper.ScreeningPlanMapper;
import com.project.platform.service.ScreeningPlanService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 放映计划
 */
@Service
public class ScreeningPlanServiceImpl implements ScreeningPlanService {
    @Resource
    private ScreeningPlanMapper screeningPlanMapper;

    @Resource
    private MovieRoomMapper movieRoomMapper;

    @Resource
    private MovieMapper movieMapper;

    @Override
    public PageVO<ScreeningPlan> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<ScreeningPlan> page = new PageVO();
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getType(), "CINEMA")) {
            query.put("cinemaId", currentUser.getId());
        }
        List<ScreeningPlan> list = screeningPlanMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(screeningPlanMapper.queryCount(query));
        return page;
    }

    @Override
    public ScreeningPlan selectById(Integer id) {
        return screeningPlanMapper.selectById(id);
    }

    @Override
    public List<ScreeningPlan> list() {
        return screeningPlanMapper.list();
    }

    @Override
    public void insert(ScreeningPlan entity) {
        CurrentUserDTO currentUser = requireCurrentUser();
        if (!Objects.equals(currentUser.getType(), "ADMIN") && !Objects.equals(currentUser.getType(), "CINEMA")) {
            throw new CustomException("当前账号无权维护排片");
        }
        ScreeningPlan target = buildTarget(entity, null, currentUser);
        screeningPlanMapper.insert(target);
    }

    @Override
    public void updateById(ScreeningPlan entity) {
        if (entity == null || entity.getId() == null) {
            throw new CustomException("放映计划ID不能为空");
        }
        CurrentUserDTO currentUser = requireCurrentUser();
        ScreeningPlan existing = screeningPlanMapper.selectById(entity.getId());
        if (existing == null) {
            throw new CustomException("放映计划不存在");
        }
        if (Objects.equals(currentUser.getType(), "CINEMA") && !Objects.equals(existing.getCinemaId(), currentUser.getId())) {
            throw new CustomException("只能维护当前影院自己的排片");
        }
        ScreeningPlan target = buildTarget(entity, existing, currentUser);
        screeningPlanMapper.updateById(target);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        CurrentUserDTO currentUser = requireCurrentUser();
        if (Objects.equals(currentUser.getType(), "ADMIN")) {
            screeningPlanMapper.removeByIds(ids);
            return;
        }
        if (!Objects.equals(currentUser.getType(), "CINEMA")) {
            throw new CustomException("当前账号无权删除排片");
        }
        for (Integer id : ids) {
            ScreeningPlan screeningPlan = screeningPlanMapper.selectById(id);
            if (screeningPlan != null && !Objects.equals(screeningPlan.getCinemaId(), currentUser.getId())) {
                throw new CustomException("只能删除当前影院自己的排片");
            }
        }
        screeningPlanMapper.removeByIds(ids);
    }

    private ScreeningPlan buildTarget(ScreeningPlan entity, ScreeningPlan existing, CurrentUserDTO currentUser) {
        ScreeningPlan target = existing == null ? new ScreeningPlan() : existing;
        if (entity != null) {
            if (entity.getId() != null) {
                target.setId(entity.getId());
            }
            if (entity.getMovieRoomId() != null) {
                target.setMovieRoomId(entity.getMovieRoomId());
            }
            if (entity.getMovieId() != null) {
                target.setMovieId(entity.getMovieId());
            }
            if (entity.getStartTime() != null) {
                target.setStartTime(entity.getStartTime());
            }
            if (entity.getEndTime() != null) {
                target.setEndTime(entity.getEndTime());
            }
            if (entity.getPrice() != null) {
                target.setPrice(entity.getPrice());
            }
            if (entity.getCinemaId() != null) {
                target.setCinemaId(entity.getCinemaId());
            }
        }

        if (target.getMovieRoomId() == null) {
            throw new CustomException("影厅不能为空");
        }
        if (target.getMovieId() == null) {
            throw new CustomException("电影不能为空");
        }
        if (target.getStartTime() == null || target.getEndTime() == null) {
            throw new CustomException("放映时间不能为空");
        }
        if (!target.getEndTime().isAfter(target.getStartTime())) {
            throw new CustomException("结束时间必须晚于开始时间");
        }
        if (target.getPrice() == null || target.getPrice() <= 0) {
            throw new CustomException("票价必须大于0");
        }

        MovieRoom movieRoom = movieRoomMapper.selectById(target.getMovieRoomId());
        if (movieRoom == null) {
            throw new CustomException("影厅不存在");
        }
        Movie movie = movieMapper.selectById(target.getMovieId());
        if (movie == null) {
            throw new CustomException("电影不存在");
        }

        target.setCinemaId(movieRoom.getCinemaId());
        if (Objects.equals(currentUser.getType(), "CINEMA") && !Objects.equals(target.getCinemaId(), currentUser.getId())) {
            throw new CustomException("只能为当前影院自己的影厅排片");
        }

        ensureNoOverlap(target);
        return target;
    }

    private void ensureNoOverlap(ScreeningPlan target) {
        List<ScreeningPlan> allPlans = screeningPlanMapper.list();
        for (ScreeningPlan item : allPlans) {
            if (Objects.equals(item.getId(), target.getId())) {
                continue;
            }
            if (!Objects.equals(item.getMovieRoomId(), target.getMovieRoomId())) {
                continue;
            }
            if (isOverlap(target.getStartTime(), target.getEndTime(), item.getStartTime(), item.getEndTime())) {
                throw new CustomException("同一影厅在该时间段已有排片");
            }
        }
    }

    private boolean isOverlap(LocalDateTime startTime, LocalDateTime endTime,
                              LocalDateTime otherStartTime, LocalDateTime otherEndTime) {
        return startTime.isBefore(otherEndTime) && endTime.isAfter(otherStartTime);
    }

    private CurrentUserDTO requireCurrentUser() {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        return currentUser;
    }
}

package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.MovieRoom;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.CinemaMapper;
import com.project.platform.mapper.MovieRoomMapper;
import com.project.platform.service.MovieRoomService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 电影影厅
 */
@Service
public class MovieRoomServiceImpl implements MovieRoomService {
    @Resource
    private MovieRoomMapper movieRoomMapper;

    @Resource
    private CinemaMapper cinemaMapper;

    @Override
    public PageVO<MovieRoom> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieRoom> page = new PageVO();
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getType(), "CINEMA")) {
            query.put("cinemaId", currentUser.getId());
        }
        List<MovieRoom> list = movieRoomMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieRoomMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieRoom selectById(Integer id) {
        return movieRoomMapper.selectById(id);
    }

    @Override
    public List<MovieRoom> list() {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getType(), "CINEMA")) {
            return movieRoomMapper.listByCinemaId(currentUser.getId());
        }
        return movieRoomMapper.list();
    }

    @Override
    public void insert(MovieRoom entity) {
        CurrentUserDTO currentUser = requireCurrentUser();
        if (Objects.equals(currentUser.getType(), "CINEMA")) {
            entity.setCinemaId(currentUser.getId());
        } else if (!Objects.equals(currentUser.getType(), "ADMIN")) {
            throw new CustomException("当前账号无权维护影厅");
        }
        check(entity);
        movieRoomMapper.insert(entity);
    }

    @Override
    public void updateById(MovieRoom entity) {
        if (entity == null || entity.getId() == null) {
            throw new CustomException("影厅ID不能为空");
        }
        CurrentUserDTO currentUser = requireCurrentUser();
        MovieRoom existing = movieRoomMapper.selectById(entity.getId());
        if (existing == null) {
            throw new CustomException("影厅不存在");
        }
        if (Objects.equals(currentUser.getType(), "CINEMA") && !Objects.equals(existing.getCinemaId(), currentUser.getId())) {
            throw new CustomException("只能维护当前影院自己的影厅");
        }
        if (entity.getCinemaId() == null) {
            entity.setCinemaId(existing.getCinemaId());
        }
        if (Objects.equals(currentUser.getType(), "CINEMA")) {
            entity.setCinemaId(currentUser.getId());
        }
        check(entity);
        movieRoomMapper.updateById(entity);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        CurrentUserDTO currentUser = requireCurrentUser();
        if (Objects.equals(currentUser.getType(), "ADMIN")) {
            movieRoomMapper.removeByIds(ids);
            return;
        }
        if (!Objects.equals(currentUser.getType(), "CINEMA")) {
            throw new CustomException("当前账号无权删除影厅");
        }
        for (Integer id : ids) {
            MovieRoom movieRoom = movieRoomMapper.selectById(id);
            if (movieRoom != null && !Objects.equals(movieRoom.getCinemaId(), currentUser.getId())) {
                throw new CustomException("只能删除当前影院自己的影厅");
            }
        }
        movieRoomMapper.removeByIds(ids);
    }

    private void check(MovieRoom entity) {
        if (entity == null) {
            throw new CustomException("影厅信息不能为空");
        }
        if (entity.getCinemaId() == null || cinemaMapper.selectById(entity.getCinemaId()) == null) {
            throw new CustomException("请选择所属电影院");
        }
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new CustomException("影厅名称不能为空");
        }
        if (entity.getNumberOfX() == null || entity.getNumberOfX() <= 0) {
            throw new CustomException("座位排数必须大于0");
        }
        if (entity.getNumberOfY() == null || entity.getNumberOfY() <= 0) {
            throw new CustomException("每排座位数必须大于0");
        }
        if (entity.getLabel() != null && entity.getLabel().length() > 100) {
            throw new CustomException("影厅标签不能超过100字");
        }
    }

    private CurrentUserDTO requireCurrentUser() {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        return currentUser;
    }
}

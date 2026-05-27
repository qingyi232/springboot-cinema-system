package com.project.platform.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.UpdatePasswordDTO;
import com.project.platform.entity.Cinema;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.CinemaMapper;
import com.project.platform.service.CinemaService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.utils.PasswordUtils;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 电影院
 */
@Service
public class CinemaServiceImpl implements CinemaService {
    private static final String DEFAULT_CINEMA_AVATAR_URL = "http://localhost:1000/file/3cbb94f9bdf6529b84e2123ed8a74d62.jpg";

    @Resource
    private CinemaMapper cinemaMapper;

    @Value("${resetPassword}")
    private String resetPassword;

    @Override
    public PageVO<Cinema> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Cinema> page = new PageVO();
        List<Cinema> list = cinemaMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(cinemaMapper.queryCount(query));
        return page;
    }

    @Override
    public Cinema selectById(Integer id) {
        return cinemaMapper.selectById(id);
    }

    @Override
    public Cinema selectByTel(String tel) {
        return cinemaMapper.selectByTel(tel);
    }

    @Override
    public List<Cinema> list() {
        return cinemaMapper.list();
    }

    @Override
    public void insert(Cinema entity) {
        check(entity);
        entity.setAvatarUrl(normalizeAvatarUrl(entity.getAvatarUrl()));
        if (entity.getPassword() == null) {
            entity.setPassword(resetPassword);
        }
        entity.setPassword(PasswordUtils.encode(entity.getPassword()));
        cinemaMapper.insert(entity);
    }

    @Override
    public void updateById(Cinema entity) {
        check(entity);
        cinemaMapper.updateById(entity);
    }

    private void check(Cinema entity) {
        if (entity == null) {
            throw new CustomException("影院信息不能为空");
        }
        if (isBlank(entity.getUsername())) {
            throw new CustomException("用户名不能为空");
        }
        if (isBlank(entity.getNickname())) {
            throw new CustomException("影院名称不能为空");
        }
        if (isBlank(entity.getAddress())) {
            throw new CustomException("影院地址不能为空");
        }
        if (entity.getLabel() != null && entity.getLabel().length() > 100) {
            throw new CustomException("影院标签过长");
        }
        Cinema cinema = cinemaMapper.selectByUserName(entity.getUsername());
        if (cinema != null && !cinema.getId().equals(entity.getId())) {
            throw new CustomException("用户名已存在");
        }
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        cinemaMapper.removeByIds(ids);
    }

    @Override
    public CurrentUserDTO login(String username, String password) {
        Cinema cinema = cinemaMapper.selectByUserName(username);
        if (cinema == null || !PasswordUtils.matches(password, cinema.getPassword())) {
            throw new CustomException("用户名或密码错误");
        }
        if (cinema.getStatus().equals("禁用")) {
            throw new CustomException("用户已禁用");
        }
        upgradePasswordIfNecessary(cinema, password);
        CurrentUserDTO currentUserDTO = new CurrentUserDTO();
        BeanUtils.copyProperties(cinema, currentUserDTO);
        return currentUserDTO;
    }

    @Override
    public void register(JSONObject data) {
        Cinema cinema = new Cinema();
        cinema.setUsername(data.getString("username"));
        cinema.setNickname(data.getString("nickname"));
        cinema.setAvatarUrl(normalizeAvatarUrl(data.getString("avatarUrl")));
        cinema.setPassword(data.getString("password"));
        cinema.setAddress(data.getString("address"));
        cinema.setLabel(data.getString("label"));
        cinema.setStatus("启用");
        insert(cinema);
    }

    @Override
    public void updateCurrentUserInfo(CurrentUserDTO currentUserDTO) {
        Cinema cinema = cinemaMapper.selectById(currentUserDTO.getId());
        cinema.setId(currentUserDTO.getId());
        cinema.setNickname(currentUserDTO.getNickname());
        cinema.setAvatarUrl(currentUserDTO.getAvatarUrl());
        cinema.setTel(currentUserDTO.getTel());
        cinema.setEmail(currentUserDTO.getEmail());
        cinema.setAddress(currentUserDTO.getAddress());
        cinema.setLabel(currentUserDTO.getLabel());
        cinemaMapper.updateById(cinema);
    }

    @Override
    public void updateCurrentUserPassword(UpdatePasswordDTO updatePassword) {
        Cinema cinema = cinemaMapper.selectById(CurrentUserThreadLocal.getCurrentUser().getId());
        if (!PasswordUtils.matches(updatePassword.getOldPassword(), cinema.getPassword())) {
            throw new CustomException("旧密码不正确");
        }
        cinema.setPassword(PasswordUtils.encode(updatePassword.getNewPassword()));
        cinemaMapper.updateById(cinema);
    }

    @Override
    public void resetPassword(Integer id) {
        Cinema cinema = cinemaMapper.selectById(id);
        cinema.setPassword(PasswordUtils.encode(resetPassword));
        cinemaMapper.updateById(cinema);
    }

    @Override
    public void assertTelExists(String tel) {
        if (tel == null || tel.isBlank()) {
            throw new CustomException("手机号不能为空");
        }
        if (cinemaMapper.selectByTel(tel) == null) {
            throw new CustomException("手机号不存在");
        }
    }


    @Override
    public void retrievePassword(RetrievePasswordDTO retrievePasswordDTO) {
        if (retrievePasswordDTO.getPassword() == null || retrievePasswordDTO.getPassword().isBlank()) {
            throw new CustomException("新密码不能为空");
        }
        Cinema cinema = cinemaMapper.selectByTel(retrievePasswordDTO.getTel());
        if (cinema == null) {
            throw new CustomException("手机号不存在");
        }
        cinema.setPassword(PasswordUtils.encode(retrievePasswordDTO.getPassword()));
        cinemaMapper.updateById(cinema);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeAvatarUrl(String avatarUrl) {
        return isBlank(avatarUrl) ? DEFAULT_CINEMA_AVATAR_URL : avatarUrl.trim();
    }

    private void upgradePasswordIfNecessary(Cinema cinema, String rawPassword) {
        if (!PasswordUtils.needsUpgrade(cinema.getPassword())) {
            return;
        }
        cinema.setPassword(PasswordUtils.encode(rawPassword));
        cinemaMapper.updateById(cinema);
    }
}

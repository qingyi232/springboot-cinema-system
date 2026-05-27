package com.project.platform.service;

import com.project.platform.entity.User;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 普通患者
 */
public interface UserService  extends CommonService{

    PageVO<User> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    User selectById(Integer id);

    List<User> list();

    void insert(User entity);

    void updateById(User entity);

    void removeByIds(List<Integer> id);

    void topUp(Integer userId, Float amount);

    void consumption(Integer userId, Float amount);

    User selectByTel(String tel);
}

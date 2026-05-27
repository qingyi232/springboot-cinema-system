package com.project.platform.service;

import com.project.platform.entity.MovieOrder;
import com.project.platform.vo.MovieOrderSubmitRequest;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影票订单
 */
public interface MovieOrderService {

    PageVO<MovieOrder> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieOrder selectById(Integer id);

    List<MovieOrder> list();

    List<MovieOrder> listSoldSeats(Integer screeningPlanId);

    void insert(MovieOrder entity);

    void updateById(MovieOrder entity);

    void removeByIds(List<Integer> id);

    void reserve(MovieOrderSubmitRequest request);

    void submit(MovieOrderSubmitRequest request);

    void pay(Integer id);

    void pay(Integer id, String paymentMethod);

    void cancel(Integer id);

    void confirm(Integer id);

    MovieOrder verifyByCode(String ticketCode);
}

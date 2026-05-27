package com.project.platform.mapper;

import com.project.platform.entity.MovieOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MovieOrderMapper {
    List<MovieOrder> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM movie_order WHERE id = #{id}")
    MovieOrder selectById(Integer id);

    @Select("SELECT * FROM movie_order")
    List<MovieOrder> list();

    @Select("SELECT * FROM movie_order WHERE screening_plan_id = #{screeningPlanId}")
    List<MovieOrder> selectByScreeningPlanId(Integer screeningPlanId);

    @Select("SELECT * FROM movie_order WHERE status = '待支付' AND reserve_expire_time IS NOT NULL AND reserve_expire_time < #{now}")
    List<MovieOrder> selectExpiredReserveOrders(LocalDateTime now);

    @Select("SELECT * FROM movie_order WHERE ticket_code = #{ticketCode} LIMIT 1")
    MovieOrder selectByTicketCode(String ticketCode);

    int insert(MovieOrder entity);

    int updateById(MovieOrder entity);

    boolean removeByIds(List<Integer> ids);

    /**
     * 查询最近已完成
     */
    @Select("SELECT * FROM movie_order WHERE status='已完成' and create_time >= DATE_SUB(NOW(), INTERVAL #{day} DAY)")
    List<MovieOrder> selectRecentlyCompleted(Integer day);

    @Select("SELECT * FROM movie_order WHERE cinema_id = #{cinemaId} and status='已完成' and create_time >= DATE_SUB(NOW(), INTERVAL #{day} DAY)")
    List<MovieOrder> selectRecentlyCompletedByShopId(Integer day, Integer cinemaId);
}

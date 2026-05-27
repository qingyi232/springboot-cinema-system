package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Movie;
import com.project.platform.entity.MovieOrder;
import com.project.platform.entity.ScreeningPlan;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieMapper;
import com.project.platform.mapper.MovieOrderMapper;
import com.project.platform.mapper.ScreeningPlanMapper;
import com.project.platform.service.MovieOrderService;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.MovieOrderSubmitRequest;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 电影票订单
 */
@Service
public class MovieOrderServiceImpl implements MovieOrderService {
    private static final String STATUS_PENDING = "待支付";
    private static final String STATUS_WAIT_VERIFY = "待取票";
    private static final String STATUS_DONE = "已完成";
    private static final String STATUS_CANCELLED = "已取消";
    private static final int RESERVE_EXPIRE_MINUTES = 15;
    private static final Set<String> ALLOWED_PAYMENT_METHODS = Set.of("支付宝", "微信", "银行卡");
    private static final DateTimeFormatter TICKET_CODE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private MovieOrderMapper movieOrderMapper;
    @Resource
    private MovieMapper movieMapper;
    @Resource
    private ScreeningPlanMapper screeningPlanMapper;
    @Resource
    private UserService userService;

    @Override
    public PageVO<MovieOrder> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        cleanupExpiredOrders();
        PageVO<MovieOrder> page = new PageVO();
        if (CurrentUserThreadLocal.getCurrentUser().getType().equals("USER")) {
            query.put("userId", CurrentUserThreadLocal.getCurrentUser().getId());
        } else if (CurrentUserThreadLocal.getCurrentUser().getType().equals("CINEMA")) {
            query.put("cinemaId", CurrentUserThreadLocal.getCurrentUser().getId());
        }
        List<MovieOrder> list = movieOrderMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieOrderMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieOrder selectById(Integer id) {
        cleanupExpiredOrders();
        return movieOrderMapper.selectById(id);
    }

    @Override
    public List<MovieOrder> list() {
        cleanupExpiredOrders();
        return movieOrderMapper.list();
    }

    @Override
    public List<MovieOrder> listSoldSeats(Integer screeningPlanId) {
        if (screeningPlanId == null) {
            throw new CustomException("放映计划不能为空");
        }
        cleanupExpiredOrders();
        return movieOrderMapper.selectByScreeningPlanId(screeningPlanId).stream()
                .filter(this::isSeatOccupied)
                .toList();
    }

    @Override
    public void insert(MovieOrder entity) {
        Integer userId = assertCurrentUserIsUser();
        entity.setUserId(userId);
        ScreeningPlan screeningPlan = getRequiredScreeningPlan(entity.getScreeningPlanId());
        fillOrderSnapshot(entity, screeningPlan);
        entity.setStatus(STATUS_PENDING);
        check(entity);
        movieOrderMapper.insert(entity);
    }

    @Override
    public void updateById(MovieOrder entity) {
        if (entity == null || entity.getId() == null) {
            throw new CustomException("订单ID不能为空");
        }
        MovieOrder existing = movieOrderMapper.selectById(entity.getId());
        if (existing == null) {
            throw new CustomException("订单不存在");
        }
        boolean seatChanged = !Objects.equals(existing.getScreeningPlanId(), entity.getScreeningPlanId())
                || !Objects.equals(existing.getNumberOfX(), entity.getNumberOfX())
                || !Objects.equals(existing.getNumberOfY(), entity.getNumberOfY());
        if (seatChanged) {
            check(entity);
        }
        movieOrderMapper.updateById(entity);
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
            movieOrderMapper.removeByIds(ids);
            return;
        }
        for (Integer id : ids) {
            MovieOrder order = movieOrderMapper.selectById(id);
            if (order == null) {
                continue;
            }
            if ("USER".equals(currentUser.getType()) && !Objects.equals(order.getUserId(), currentUser.getId())) {
                throw new CustomException("无权删除其他用户订单");
            }
            if ("CINEMA".equals(currentUser.getType()) && !Objects.equals(order.getCinemaId(), currentUser.getId())) {
                throw new CustomException("无权删除其他影院订单");
            }
            if (!"USER".equals(currentUser.getType()) && !"CINEMA".equals(currentUser.getType())) {
                throw new CustomException("当前账号无权删除订单");
            }
        }
        movieOrderMapper.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reserve(MovieOrderSubmitRequest request) {
        createOrders(request, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(MovieOrderSubmitRequest request) {
        createOrders(request, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Integer id) {
        pay(id, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Integer id, String paymentMethod) {
        cleanupExpiredOrders();
        MovieOrder movieOrder = selectRequiredOrder(id);
        assertUserOwnsOrder(movieOrder);
        ensurePendingOrderCanOperate(movieOrder);
        String finalPaymentMethod = paymentMethod;
        if (finalPaymentMethod == null || finalPaymentMethod.trim().isEmpty()) {
            finalPaymentMethod = movieOrder.getPaymentMethod();
        }
        validatePaymentMethod(finalPaymentMethod);
        userService.consumption(movieOrder.getUserId(), movieOrder.getPrice());
        movieOrder.setPaymentMethod(finalPaymentMethod);
        movieOrder.setStatus(STATUS_WAIT_VERIFY);
        movieOrder.setReserveExpireTime(null);
        movieOrder.setTicketCode(generateTicketCode());
        updateById(movieOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Integer id) {
        cleanupExpiredOrders();
        MovieOrder movieOrder = selectRequiredOrder(id);
        assertUserOwnsOrder(movieOrder);
        if (STATUS_PENDING.equals(movieOrder.getStatus())) {
            movieOrder.setStatus(STATUS_CANCELLED);
            movieOrder.setReserveExpireTime(null);
            movieOrder.setTicketCode(null);
            movieOrder.setVerifyTime(null);
            updateById(movieOrder);
            return;
        }
        if (STATUS_WAIT_VERIFY.equals(movieOrder.getStatus())) {
            userService.topUp(movieOrder.getUserId(), movieOrder.getPrice());
            movieOrder.setStatus(STATUS_CANCELLED);
            movieOrder.setReserveExpireTime(null);
            movieOrder.setTicketCode(null);
            movieOrder.setVerifyTime(null);
            updateById(movieOrder);
            return;
        }
        throw new CustomException("当前状态暂不支持取消");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Integer id) {
        cleanupExpiredOrders();
        MovieOrder movieOrder = selectRequiredOrder(id);
        assertUserOwnsOrder(movieOrder);
        completeOrder(movieOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MovieOrder verifyByCode(String ticketCode) {
        cleanupExpiredOrders();
        if (ticketCode == null || ticketCode.trim().isEmpty()) {
            throw new CustomException("票码不能为空");
        }
        if (CurrentUserThreadLocal.getCurrentUser() == null) {
            throw new CustomException("请先登录");
        }
        String userType = CurrentUserThreadLocal.getCurrentUser().getType();
        if (!"ADMIN".equals(userType) && !"CINEMA".equals(userType)) {
            throw new CustomException("当前账号无权核销");
        }
        String finalTicketCode = ticketCode.trim();
        MovieOrder movieOrder = movieOrderMapper.selectByTicketCode(finalTicketCode);
        if (movieOrder == null) {
            throw new CustomException("票码不存在");
        }
        if ("CINEMA".equals(userType)
                && !Objects.equals(movieOrder.getCinemaId(), CurrentUserThreadLocal.getCurrentUser().getId())) {
            throw new CustomException("无权核销其他影院订单");
        }
        completeOrder(movieOrder);
        List<MovieOrder> refreshedList = movieOrderMapper.queryPage(0, 1, Map.of("ticketCode", finalTicketCode));
        return refreshedList.isEmpty() ? movieOrderMapper.selectById(movieOrder.getId()) : refreshedList.get(0);
    }

    private void createOrders(MovieOrderSubmitRequest request, boolean payNow) {
        cleanupExpiredOrders();
        Integer userId = assertCurrentUserIsUser();
        validateSubmitRequest(request, payNow);
        ScreeningPlan screeningPlan = getRequiredScreeningPlan(request.getScreeningPlanId());
        List<MovieOrder> orderList = buildOrderDrafts(request, screeningPlan, userId, payNow);
        if (payNow) {
            userService.consumption(userId, screeningPlan.getPrice() * orderList.size());
        }
        for (MovieOrder order : orderList) {
            movieOrderMapper.insert(order);
        }
    }

    private List<MovieOrder> buildOrderDrafts(MovieOrderSubmitRequest request,
                                              ScreeningPlan screeningPlan,
                                              Integer userId,
                                              boolean payNow) {
        Set<String> seatSet = new HashSet<>();
        List<MovieOrder> orderList = new ArrayList<>();
        for (MovieOrderSubmitRequest.SeatItem seat : request.getSeats()) {
            if (seat == null || seat.getNumberOfX() == null || seat.getNumberOfY() == null) {
                throw new CustomException("座位信息不能为空");
            }
            String seatKey = seat.getNumberOfX() + "-" + seat.getNumberOfY();
            if (!seatSet.add(seatKey)) {
                throw new CustomException("座位选择重复，请重新确认");
            }
            MovieOrder movieOrder = new MovieOrder();
            movieOrder.setUserId(userId);
            movieOrder.setScreeningPlanId(request.getScreeningPlanId());
            movieOrder.setNumberOfX(String.valueOf(seat.getNumberOfX()));
            movieOrder.setNumberOfY(seat.getNumberOfY());
            fillOrderSnapshot(movieOrder, screeningPlan);
            if (payNow) {
                movieOrder.setPaymentMethod(request.getPaymentMethod());
                movieOrder.setStatus(STATUS_WAIT_VERIFY);
                movieOrder.setTicketCode(generateTicketCode());
                movieOrder.setReserveExpireTime(null);
            } else {
                if (request.getPaymentMethod() != null && !request.getPaymentMethod().trim().isEmpty()) {
                    validatePaymentMethod(request.getPaymentMethod());
                    movieOrder.setPaymentMethod(request.getPaymentMethod());
                }
                movieOrder.setStatus(STATUS_PENDING);
                movieOrder.setReserveExpireTime(LocalDateTime.now().plusMinutes(RESERVE_EXPIRE_MINUTES));
            }
            check(movieOrder);
            orderList.add(movieOrder);
        }
        return orderList;
    }

    private void check(MovieOrder entity) {
        if (entity.getScreeningPlanId() == null) {
            throw new CustomException("放映计划不能为空");
        }
        if (entity.getNumberOfX() == null || entity.getNumberOfY() == null) {
            throw new CustomException("座位信息不能为空");
        }
        List<MovieOrder> occupiedOrderList = movieOrderMapper.selectByScreeningPlanId(entity.getScreeningPlanId());
        boolean occupied = occupiedOrderList.stream()
                .filter(this::isSeatOccupied)
                .anyMatch(item -> Objects.equals(item.getNumberOfX(), entity.getNumberOfX())
                        && Objects.equals(item.getNumberOfY(), entity.getNumberOfY())
                        && !Objects.equals(item.getId(), entity.getId()));
        if (occupied) {
            throw new CustomException("座位已被占用，请重新选座");
        }
    }

    private boolean isSeatOccupied(MovieOrder order) {
        if (order == null || STATUS_CANCELLED.equals(order.getStatus())) {
            return false;
        }
        if (STATUS_WAIT_VERIFY.equals(order.getStatus()) || STATUS_DONE.equals(order.getStatus())) {
            return true;
        }
        if (!STATUS_PENDING.equals(order.getStatus())) {
            return false;
        }
        if (order.getReserveExpireTime() == null) {
            return false;
        }
        return order.getReserveExpireTime().isAfter(LocalDateTime.now());
    }

    private void ensurePendingOrderCanOperate(MovieOrder movieOrder) {
        if (!STATUS_PENDING.equals(movieOrder.getStatus())) {
            throw new CustomException("数据已过期，请先刷新页面");
        }
        if (movieOrder.getReserveExpireTime() == null || !movieOrder.getReserveExpireTime().isAfter(LocalDateTime.now())) {
            movieOrder.setStatus(STATUS_CANCELLED);
            movieOrderMapper.updateById(movieOrder);
            throw new CustomException("预约锁座已超时，请重新选座");
        }
    }

    private void completeOrder(MovieOrder movieOrder) {
        if (!STATUS_WAIT_VERIFY.equals(movieOrder.getStatus())) {
            throw new CustomException("数据已过期，请先刷新页面");
        }
        movieOrder.setStatus(STATUS_DONE);
        movieOrder.setReserveExpireTime(null);
        movieOrder.setVerifyTime(LocalDateTime.now());
        Movie movie = movieMapper.selectById(movieOrder.getMovieId());
        float currentBoxOffice = movie.getBoxOffice() == null ? 0F : movie.getBoxOffice();
        movie.setBoxOffice(currentBoxOffice + movieOrder.getPrice());
        movieMapper.updateById(movie);
        updateById(movieOrder);
    }

    private void cleanupExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<MovieOrder> expiredOrders = movieOrderMapper.list().stream()
                .filter(order -> STATUS_PENDING.equals(order.getStatus()))
                .filter(order -> order.getReserveExpireTime() == null || !order.getReserveExpireTime().isAfter(now))
                .toList();
        for (MovieOrder order : expiredOrders) {
            order.setStatus(STATUS_CANCELLED);
            movieOrderMapper.updateById(order);
        }
    }

    private MovieOrder selectRequiredOrder(Integer id) {
        MovieOrder movieOrder = movieOrderMapper.selectById(id);
        if (movieOrder == null) {
            throw new CustomException("订单不存在");
        }
        return movieOrder;
    }

    private void assertUserOwnsOrder(MovieOrder movieOrder) {
        Integer currentUserId = assertCurrentUserIsUser();
        if (!Objects.equals(movieOrder.getUserId(), currentUserId)) {
            throw new CustomException("无权操作该订单");
        }
    }

    private Integer assertCurrentUserIsUser() {
        if (CurrentUserThreadLocal.getCurrentUser() == null) {
            throw new CustomException("请先登录");
        }
        if (!"USER".equals(CurrentUserThreadLocal.getCurrentUser().getType())) {
            throw new CustomException("只有普通用户才能操作订单");
        }
        return CurrentUserThreadLocal.getCurrentUser().getId();
    }

    private ScreeningPlan getRequiredScreeningPlan(Integer screeningPlanId) {
        if (screeningPlanId == null) {
            throw new CustomException("放映计划不能为空");
        }
        ScreeningPlan screeningPlan = screeningPlanMapper.selectById(screeningPlanId);
        if (screeningPlan == null) {
            throw new CustomException("放映计划不存在");
        }
        return screeningPlan;
    }

    private void fillOrderSnapshot(MovieOrder entity, ScreeningPlan screeningPlan) {
        entity.setCinemaId(screeningPlan.getCinemaId());
        entity.setMovieId(screeningPlan.getMovieId());
        entity.setMovieRoomId(screeningPlan.getMovieRoomId());
        entity.setStartTime(screeningPlan.getStartTime());
        entity.setEndTime(screeningPlan.getEndTime());
        entity.setPrice(screeningPlan.getPrice());
    }

    private void validateSubmitRequest(MovieOrderSubmitRequest request, boolean requirePaymentMethod) {
        if (request == null) {
            throw new CustomException("提交信息不能为空");
        }
        if (request.getSeats() == null || request.getSeats().isEmpty()) {
            throw new CustomException("请先选择座位");
        }
        if (requirePaymentMethod) {
            validatePaymentMethod(request.getPaymentMethod());
        } else if (request.getPaymentMethod() != null && !request.getPaymentMethod().trim().isEmpty()) {
            validatePaymentMethod(request.getPaymentMethod());
        }
    }

    private void validatePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new CustomException("请选择支付方式");
        }
        if (!ALLOWED_PAYMENT_METHODS.contains(paymentMethod)) {
            throw new CustomException("支付方式不支持");
        }
    }

    private String generateTicketCode() {
        for (int i = 0; i < 10; i++) {
            String ticketCode = "TK" + LocalDateTime.now().format(TICKET_CODE_TIME_FORMATTER)
                    + ThreadLocalRandom.current().nextInt(100, 999);
            if (movieOrderMapper.selectByTicketCode(ticketCode) == null) {
                return ticketCode;
            }
        }
        throw new CustomException("票码生成失败，请稍后重试");
    }
}

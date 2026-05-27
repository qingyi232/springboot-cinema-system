package com.project.platform.entity;

import java.time.LocalDateTime;

/**
 * 电影票订单
 */
public class MovieOrder {
    /**
     * id
     */
    private Integer id;
    /**
     * 影院
     */
    private Integer cinemaId;
    /**
     * 影院名称
     */
    private String cinemaName;
    /**
     * 影厅
     */
    private Integer movieRoomId;
    /**
     * 影厅名称
     */
    private String movieRoomName;
    /**
     * 电影
     */
    private Integer movieId;
    /**
     * 电影名称
     */
    private String movieName;

    /**
     * 电影主图
     */
    private String movieMainImg;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 价格
     */
    private Float price;
    /**
     * 座位列数
     */
    private String numberOfX;
    /**
     * 座位行数
     */
    private Integer numberOfY;
    /**
     * 放映计划ID(用于查看已卖的座位)
     */
    private Integer screeningPlanId;
    /**
     * 状态
     */
    private String status;
    /**
     * 支付方式
     */
    private String paymentMethod;
    /**
     * 预约锁座过期时间
     */
    private LocalDateTime reserveExpireTime;
    /**
     * 票码
     */
    private String ticketCode;
    /**
     * 核销时间
     */
    private LocalDateTime verifyTime;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 评价Id
     */
    private Integer orderEvaluateId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Integer cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public Integer getMovieRoomId() {
        return movieRoomId;
    }

    public void setMovieRoomId(Integer movieRoomId) {
        this.movieRoomId = movieRoomId;
    }

    public String getMovieRoomName() {
        return movieRoomName;
    }

    public void setMovieRoomName(String movieRoomName) {
        this.movieRoomName = movieRoomName;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieMainImg() {
        return movieMainImg;
    }

    public void setMovieMainImg(String movieMainImg) {
        this.movieMainImg = movieMainImg;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getNumberOfX() {
        return numberOfX;
    }

    public void setNumberOfX(String numberOfX) {
        this.numberOfX = numberOfX;
    }

    public Integer getNumberOfY() {
        return numberOfY;
    }

    public void setNumberOfY(Integer numberOfY) {
        this.numberOfY = numberOfY;
    }

    public Integer getScreeningPlanId() {
        return screeningPlanId;
    }

    public void setScreeningPlanId(Integer screeningPlanId) {
        this.screeningPlanId = screeningPlanId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getReserveExpireTime() {
        return reserveExpireTime;
    }

    public void setReserveExpireTime(LocalDateTime reserveExpireTime) {
        this.reserveExpireTime = reserveExpireTime;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public LocalDateTime getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(LocalDateTime verifyTime) {
        this.verifyTime = verifyTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getOrderEvaluateId() {
        return orderEvaluateId;
    }

    public void setOrderEvaluateId(Integer orderEvaluateId) {
        this.orderEvaluateId = orderEvaluateId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}

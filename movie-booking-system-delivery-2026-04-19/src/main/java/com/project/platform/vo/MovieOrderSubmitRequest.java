package com.project.platform.vo;

import java.util.List;

/**
 * 电影票提交订单请求
 */
public class MovieOrderSubmitRequest {
    /**
     * 放映计划ID
     */
    private Integer screeningPlanId;
    /**
     * 支付方式
     */
    private String paymentMethod;
    /**
     * 座位列表
     */
    private List<SeatItem> seats;

    public Integer getScreeningPlanId() {
        return screeningPlanId;
    }

    public void setScreeningPlanId(Integer screeningPlanId) {
        this.screeningPlanId = screeningPlanId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<SeatItem> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatItem> seats) {
        this.seats = seats;
    }

    public static class SeatItem {
        /**
         * 排号
         */
        private Integer numberOfX;
        /**
         * 座号
         */
        private Integer numberOfY;

        public Integer getNumberOfX() {
            return numberOfX;
        }

        public void setNumberOfX(Integer numberOfX) {
            this.numberOfX = numberOfX;
        }

        public Integer getNumberOfY() {
            return numberOfY;
        }

        public void setNumberOfY(Integer numberOfY) {
            this.numberOfY = numberOfY;
        }
    }
}

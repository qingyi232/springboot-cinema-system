package com.project.platform.entity;

import java.time.LocalDateTime;
/**
 * 放映计划
 */
public class ScreeningPlan  {
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

   public LocalDateTime getCreateTime() {
      return createTime;
   }

   public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
   }


}

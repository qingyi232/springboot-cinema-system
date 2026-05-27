package com.project.platform.entity;

import java.time.LocalDateTime;
/**
 * 订单评价
 */
public class MovieOrderEvaluate  {
   /**
    * id
    */
   private Integer id;
   /**
   * 用户
   */
   private Integer userId;
   /**
   * 用户名
   */
   private String username;
   /**
   * 用户头像
   */
   private String userAvatar;
   /**
   * 电影
   */
   private Integer movieId;
   /**
   * 电影名称
   */
   private String movieName;
   /**
   * 订单
   */
   private Integer movieOrderId;
   /**
   * 内容
   */
   private String content;
   /**
   * 评分
   */
   private Integer rate;
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
   public Integer getUserId() {
      return userId;
   }

   public void setUserId(Integer userId) {
      this.userId = userId;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getUserAvatar() {
      return userAvatar;
   }

   public void setUserAvatar(String userAvatar) {
      this.userAvatar = userAvatar;
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

   public Integer getMovieOrderId() {
      return movieOrderId;
   }

   public void setMovieOrderId(Integer movieOrderId) {
      this.movieOrderId = movieOrderId;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public Integer getRate() {
      return rate;
   }

   public void setRate(Integer rate) {
      this.rate = rate;
   }

   public LocalDateTime getCreateTime() {
      return createTime;
   }

   public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
   }


}

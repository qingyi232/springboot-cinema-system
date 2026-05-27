package com.project.platform.entity;

import java.time.LocalDateTime;
/**
 * 电影收藏
 */
public class MovieCollect  {
   /**
    * id
    */
   private Integer id;
   /**
   * 电影
   */
   private Integer movieId;
   /**
   * 电影名称
   */
   private String movieName;
   /**
   * 用户
   */
   private Integer userId;
   /**
   * 用户名
   */
   private String username;
   /**
   * 创建时间
   */
   private LocalDateTime createTime;

   /**
    * 电影封面图
    */
   private String movieMainImg;


   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
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

   public LocalDateTime getCreateTime() {
      return createTime;
   }

   public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public String getMovieMainImg() {
      return movieMainImg;
   }

   public void setMovieMainImg(String movieMainImg) {
      this.movieMainImg = movieMainImg;
   }
}

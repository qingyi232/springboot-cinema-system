package com.project.platform.entity;

import java.time.LocalDateTime;
/**
 * 电影影厅
 */
public class MovieRoom  {
   /**
    * id
    */
   private Integer id;
   /**
    * 名称
    */
   private String name;
   /**
    * 标签
    */
   private String label;
   /**
    * 影院
    */
   private Integer cinemaId;
   /**
    * 影院名称
    */
   private String cinemaName;
   /**
    * 座位列数
    */
   private Integer numberOfX;
   /**
    * 座位行数
    */
   private Integer numberOfY;
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
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
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

   public LocalDateTime getCreateTime() {
      return createTime;
   }

   public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
   }


}

package com.project.platform.entity;

import java.time.LocalDateTime;
/**
 * 电影院
 */
public class Cinema  {
   /**
    * id
    */
   private Integer id;
   /**
   * 用户名
   */
   private String username;
   /**
   * 密码
   */
   private String password;
   /**
   * 昵称
   */
   private String nickname;
   /**
   * 头像
   */
   private String avatarUrl;
   /**
   * 电话
   */
   private String tel;
   /**
   * 邮箱
   */
   private String email;
   /**
   * 状态
   */
   private String status;
   /**
   * 地址
   */
   private String address;
   /**
   * 标签
   */
   private String label;
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
   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getNickname() {
      return nickname;
   }

   public void setNickname(String nickname) {
      this.nickname = nickname;
   }

   public String getAvatarUrl() {
      return avatarUrl;
   }

   public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
   }

   public String getTel() {
      return tel;
   }

   public void setTel(String tel) {
      this.tel = tel;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public LocalDateTime getCreateTime() {
      return createTime;
   }

   public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
   }


}

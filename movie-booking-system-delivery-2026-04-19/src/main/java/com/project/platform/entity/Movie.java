package com.project.platform.entity;

import java.time.LocalDateTime;
/**
 * 电影信息
 */
public class Movie  {
   /**
    * id
    */
   private Integer id;
   /**
   * 名称
   */
   private String name;
   /**
   * 封面图
   */
   private String mainImg;
   /**
   * 分类
   */
   private Integer movieTypeId;
   /**
   * 分类名称
   */
   private String movieTypeName;
   /**
   * 电影年份
   */
   private Integer movieYearsId;
   /**
   * 电影年份名称
   */
   private String movieYearsName;
   /**
   * 电影区域
   */
   private Integer movieRegionId;
   /**
   * 电影区域名称
   */
   private String movieRegionName;
   /**
   * 上映时间
   */
   private String releaseDate;
   /**
   * 时长
   */
   private Integer duration;
   /**
   * 导演
   */
   private String director;
   /**
   * 主演
   */
   private String protagonist;
   /**
   * 简介
   */
   private String intro;
   /**
   * 图集
   */
   private String imgs;

   /**
    * 票房
    */
   private Float boxOffice;
   /**
    * 最低票价
    */
   private Float lowestPrice;
   /**
    * 评分
    */
   private Integer score;
   /**
   * 创建时间
   */
   private LocalDateTime createTime;

   /**
    * 收藏id
    */
   private Integer movieCollectId;
   private int weight;

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

   public String getMainImg() {
      return mainImg;
   }

   public void setMainImg(String mainImg) {
      this.mainImg = mainImg;
   }

   public Integer getMovieTypeId() {
      return movieTypeId;
   }

   public void setMovieTypeId(Integer movieTypeId) {
      this.movieTypeId = movieTypeId;
   }

   public String getMovieTypeName() {
      return movieTypeName;
   }

   public void setMovieTypeName(String movieTypeName) {
      this.movieTypeName = movieTypeName;
   }

   public Integer getMovieYearsId() {
      return movieYearsId;
   }

   public void setMovieYearsId(Integer movieYearsId) {
      this.movieYearsId = movieYearsId;
   }

   public String getMovieYearsName() {
      return movieYearsName;
   }

   public void setMovieYearsName(String movieYearsName) {
      this.movieYearsName = movieYearsName;
   }

   public Integer getMovieRegionId() {
      return movieRegionId;
   }

   public void setMovieRegionId(Integer movieRegionId) {
      this.movieRegionId = movieRegionId;
   }

   public String getMovieRegionName() {
      return movieRegionName;
   }

   public void setMovieRegionName(String movieRegionName) {
      this.movieRegionName = movieRegionName;
   }

   public String getReleaseDate() {
      return releaseDate;
   }

   public void setReleaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
   }

   public Integer getDuration() {
      return duration;
   }

   public void setDuration(Integer duration) {
      this.duration = duration;
   }

   public String getDirector() {
      return director;
   }

   public void setDirector(String director) {
      this.director = director;
   }

   public String getProtagonist() {
      return protagonist;
   }

   public void setProtagonist(String protagonist) {
      this.protagonist = protagonist;
   }

   public String getIntro() {
      return intro;
   }

   public void setIntro(String intro) {
      this.intro = intro;
   }

   public String getImgs() {
      return imgs;
   }

   public void setImgs(String imgs) {
      this.imgs = imgs;
   }


   public Float getBoxOffice() {
      return boxOffice;
   }

   public void setBoxOffice(Float boxOffice) {
      this.boxOffice = boxOffice;
   }

   public Float getLowestPrice() {
      return lowestPrice;
   }

   public void setLowestPrice(Float lowestPrice) {
      this.lowestPrice = lowestPrice;
   }

   public Integer getScore() {
      return score;
   }

   public void setScore(Integer score) {
      this.score = score;
   }

   public LocalDateTime getCreateTime() {
      return createTime;
   }

   public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public Integer getWeight() {
      return weight;
   }

   public void setWeight(Integer weight) {
      this.weight = weight;
   }

   public Integer getMovieCollectId() {
      return movieCollectId;
   }

   public void setMovieCollectId(Integer movieCollectId) {
      this.movieCollectId = movieCollectId;
   }
}

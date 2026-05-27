package com.project.platform.service;


import com.project.platform.vo.EchartsDataVO;
import com.project.platform.vo.ValueNameVO;

import java.util.List;

public interface StatisticalReportFormsService {
    /**
     * 电影类型占比
     * @return
     */
    List<ValueNameVO> getMovieTypeProportionOfChart();

    /**
     * 电影年份占比
     * @return
     */
    List<ValueNameVO> getMovieYearsProportionOfChart();

    /**
     * 电影区域占比
     * @return
     */
    List<ValueNameVO> getMovieRegionProportionOfChart();


    /**
     * 销售总额统计
     *
     * @param day
     */
    EchartsDataVO getMovieSalesTotalAmountChart(int day);
}

package com.project.platform.service.impl;

import com.project.platform.entity.MovieOrder;
import com.project.platform.mapper.MovieMapper;
import com.project.platform.mapper.MovieOrderMapper;
import com.project.platform.service.StatisticalReportFormsService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.utils.TimeUtils;
import com.project.platform.vo.EchartsDataVO;
import com.project.platform.vo.ValueNameVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticalReportFormsServiceImpl implements StatisticalReportFormsService {
    @Resource
    private MovieOrderMapper movieOrderMapper;
    @Resource
    private MovieMapper movieMapper;


    public List<ValueNameVO> getMovieTypeProportionOfChart() {
        return movieMapper.selectTypeCount();
    }


    public List<ValueNameVO> getMovieYearsProportionOfChart() {
        return movieMapper.selectYearsCount();
    }


    public List<ValueNameVO> getMovieRegionProportionOfChart() {
        return movieMapper.selectRegionCount();
    }


    public EchartsDataVO getMovieSalesTotalAmountChart(int day) {
        //查询数据库中的数据
        List<MovieOrder> movieOrderList;
        if (CurrentUserThreadLocal.getCurrentUser().getType().equals("CINEMA")) {
            movieOrderList = movieOrderMapper.selectRecentlyCompletedByShopId(day, CurrentUserThreadLocal.getCurrentUser().getId());
        } else {
            movieOrderList = movieOrderMapper.selectRecentlyCompleted(day);
        }

        List<LocalDateTime> recentSevenDays = TimeUtils.getRecentSevenDays(day);
        EchartsDataVO echartsDataVO = new EchartsDataVO();
        for (LocalDateTime localDateTime : recentSevenDays) {
            echartsDataVO.getXData().add(TimeUtils.formatterDate(localDateTime));
            float sum = 0;
            for (MovieOrder movieOrder : movieOrderList) {
                if (movieOrder.getCreateTime().isBefore(TimeUtils.setToEndOfDay(localDateTime))
                        && (movieOrder.getCreateTime().isAfter(TimeUtils.setToMidnight(localDateTime))
                        || movieOrder.getCreateTime().equals(TimeUtils.setToMidnight(localDateTime)))) {
                    sum += movieOrder.getPrice();
                }
            }
            echartsDataVO.getSeriesData().add(sum);
        }
        return echartsDataVO;
    }


}

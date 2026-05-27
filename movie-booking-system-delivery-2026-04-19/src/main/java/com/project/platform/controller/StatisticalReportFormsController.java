package com.project.platform.controller;


import com.project.platform.service.StatisticalReportFormsService;
import com.project.platform.vo.EchartsDataVO;
import com.project.platform.vo.ResponseVO;
import com.project.platform.vo.ValueNameVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statisticalReportForms")
public class StatisticalReportFormsController {

    @Resource
    private StatisticalReportFormsService statisticalReportFormsService;

    @GetMapping("movieSalesTotalAmountChart/{day}")
    public ResponseVO<EchartsDataVO> getMovieSalesTotalAmountChart(@PathVariable int day) {
        return ResponseVO.ok(statisticalReportFormsService.getMovieSalesTotalAmountChart(day));
    }

    @GetMapping("movieTypeProportionOfChar")
    public ResponseVO<List<ValueNameVO>> getMovieTypeProportionOfChart() {
        return ResponseVO.ok(statisticalReportFormsService.getMovieTypeProportionOfChart());
    }

    @GetMapping("movieYearsProportionOfChar")
    public ResponseVO<List<ValueNameVO>> getMovieYearsProportionOfChart() {
        return ResponseVO.ok(statisticalReportFormsService.getMovieYearsProportionOfChart());
    }

    @GetMapping("movieRegionProportionOfChar")
    public ResponseVO<List<ValueNameVO>> getMovieRegionProportionOfChart() {
        return ResponseVO.ok(statisticalReportFormsService.getMovieRegionProportionOfChart());
    }


}

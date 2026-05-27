package com.project.platform.vo;

import java.util.ArrayList;
import java.util.List;

public class EchartsDataVO {
    private List<Object> xData;
    private List<Object> yData;
    private List<Object> seriesData;

    public EchartsDataVO() {
        this.xData = new ArrayList<>();
        this.yData = new ArrayList<>();
        this.seriesData = new ArrayList<>();
    }

    public List<Object> getXData() {
        return xData;
    }

    public void setXData(List<Object> xData) {
        this.xData = xData;
    }

    public List<Object> getYData() {
        return yData;
    }

    public void setYData(List<Object> yData) {
        this.yData = yData;
    }

    public List<Object> getSeriesData() {
        return seriesData;
    }

    public void setSeriesData(List<Object> seriesData) {
        this.seriesData = seriesData;
    }
}

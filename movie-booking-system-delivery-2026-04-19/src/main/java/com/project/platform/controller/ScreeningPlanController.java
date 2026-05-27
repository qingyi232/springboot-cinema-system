package com.project.platform.controller;

import cn.hutool.json.JSONObject;
import com.project.platform.entity.ScreeningPlan;
import com.project.platform.mapper.ScreeningPlanMapper;
import com.project.platform.service.ScreeningPlanService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 放映计划
 */
@RestController
@RequestMapping("/screeningPlan")
public class ScreeningPlanController {
    @Resource
    private ScreeningPlanService screeningPlanService;

    @Resource
    private ScreeningPlanMapper screeningPlanMapper;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<ScreeningPlan>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<ScreeningPlan> page = screeningPlanService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<ScreeningPlan> selectById(@PathVariable("id") Integer id) {
        ScreeningPlan entity = screeningPlanService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<ScreeningPlan>> list() {
        return ResponseVO.ok(screeningPlanService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody ScreeningPlan entity) {
        screeningPlanService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody ScreeningPlan entity) {
        screeningPlanService.updateById(entity);
        return ResponseVO.ok();
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        screeningPlanService.removeByIds(ids);
        return ResponseVO.ok();
    }


    @GetMapping("selectAvailableCinema/{movieId}")
    public ResponseVO<List<JSONObject>> selectAvailableCinema(@PathVariable("movieId") Integer movieId) {
        return ResponseVO.ok(screeningPlanMapper.selectAvailableCinema(movieId));
    }

    @GetMapping("selectAvailableMovieRoom/{cinemaId}/{movieId}")
    public ResponseVO<List<JSONObject>> selectAvailableMovieRoom(@PathVariable("cinemaId") Integer cinemaId, @PathVariable("movieId") Integer movieId) {
        return ResponseVO.ok(screeningPlanMapper.selectAvailableMovieRoom(cinemaId, movieId));
    }


}

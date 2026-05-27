package com.project.platform.controller;

import com.project.platform.entity.MovieOrderEvaluate;
import com.project.platform.service.MovieOrderEvaluateService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单评价
 */
@RestController
@RequestMapping("/movieOrderEvaluate")
public class MovieOrderEvaluateController {
    @Resource
    private MovieOrderEvaluateService movieOrderEvaluateService;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<MovieOrderEvaluate>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<MovieOrderEvaluate> page = movieOrderEvaluateService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<MovieOrderEvaluate> selectById(@PathVariable("id") Integer id) {
        MovieOrderEvaluate entity = movieOrderEvaluateService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<MovieOrderEvaluate>> list() {
        return ResponseVO.ok(movieOrderEvaluateService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody MovieOrderEvaluate entity) {
        movieOrderEvaluateService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody MovieOrderEvaluate entity) {
        movieOrderEvaluateService.updateById(entity);
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
        movieOrderEvaluateService.removeByIds(ids);
        return ResponseVO.ok();
    }
}

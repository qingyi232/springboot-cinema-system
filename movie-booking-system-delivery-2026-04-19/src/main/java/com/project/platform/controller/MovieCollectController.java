package com.project.platform.controller;

import com.project.platform.entity.MovieCollect;
import com.project.platform.mapper.MovieCollectMapper;
import com.project.platform.service.MovieCollectService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 电影收藏
 */
@RestController
@RequestMapping("/movieCollect")
public class MovieCollectController {
    @Resource
    private MovieCollectService movieCollectService;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<MovieCollect>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<MovieCollect> page = movieCollectService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<MovieCollect> selectById(@PathVariable("id") Integer id) {
        MovieCollect entity = movieCollectService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<MovieCollect>> list() {
        return ResponseVO.ok(movieCollectService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO<MovieCollect> add(@RequestBody MovieCollect entity) {
        movieCollectService.insert(entity);
        return ResponseVO.ok(entity);
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody MovieCollect entity) {
        movieCollectService.updateById(entity);
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
        movieCollectService.removeByIds(ids);
        return ResponseVO.ok();
    }
}

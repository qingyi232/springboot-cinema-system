package com.project.platform.controller;

import com.project.platform.entity.MovieBrowsingHistory;
import com.project.platform.service.MovieBrowsingHistoryService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 电影浏览历史
 */
@RestController
@RequestMapping("/movieBrowsingHistory")
public class MovieBrowsingHistoryController {
    @Resource
    private MovieBrowsingHistoryService movieBrowsingHistoryService;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<MovieBrowsingHistory>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<MovieBrowsingHistory> page = movieBrowsingHistoryService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<MovieBrowsingHistory> selectById(@PathVariable("id") Integer id) {
        MovieBrowsingHistory entity = movieBrowsingHistoryService.selectById(id);
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<MovieBrowsingHistory>> list() {
        return ResponseVO.ok(movieBrowsingHistoryService.list());
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody MovieBrowsingHistory entity) {
        movieBrowsingHistoryService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody MovieBrowsingHistory entity) {
        movieBrowsingHistoryService.updateById(entity);
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
        movieBrowsingHistoryService.removeByIds(ids);
        return ResponseVO.ok();
    }
}

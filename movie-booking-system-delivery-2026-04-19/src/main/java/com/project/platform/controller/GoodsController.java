package com.project.platform.controller;

import com.project.platform.entity.Goods;
import com.project.platform.service.GoodsService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    @GetMapping("page")
    public ResponseVO<PageVO<Goods>> page(@RequestParam Map<String, Object> query,
                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseVO.ok(goodsService.page(query, pageNum, pageSize));
    }

    @GetMapping("selectById/{id}")
    public ResponseVO<Goods> selectById(@PathVariable("id") Integer id) {
        return ResponseVO.ok(goodsService.selectById(id));
    }

    @GetMapping("list")
    public ResponseVO<List<Goods>> list() {
        return ResponseVO.ok(goodsService.list());
    }

    @PostMapping("add")
    public ResponseVO add(@RequestBody Goods entity) {
        goodsService.insert(entity);
        return ResponseVO.ok();
    }

    @PutMapping("update")
    public ResponseVO update(@RequestBody Goods entity) {
        goodsService.updateById(entity);
        return ResponseVO.ok();
    }

    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        goodsService.removeByIds(ids);
        return ResponseVO.ok();
    }
}

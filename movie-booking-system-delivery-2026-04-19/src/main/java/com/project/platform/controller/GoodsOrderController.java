package com.project.platform.controller;

import com.project.platform.entity.GoodsOrder;
import com.project.platform.service.GoodsOrderService;
import com.project.platform.vo.GoodsOrderSubmitRequest;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goodsOrder")
public class GoodsOrderController {
    @Resource
    private GoodsOrderService goodsOrderService;

    @GetMapping("page")
    public ResponseVO<PageVO<GoodsOrder>> page(@RequestParam Map<String, Object> query,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseVO.ok(goodsOrderService.page(query, pageNum, pageSize));
    }

    @GetMapping("selectById/{id}")
    public ResponseVO<GoodsOrder> selectById(@PathVariable("id") Integer id) {
        return ResponseVO.ok(goodsOrderService.selectById(id));
    }

    @GetMapping("list")
    public ResponseVO<List<GoodsOrder>> list() {
        return ResponseVO.ok(goodsOrderService.list());
    }

    @PostMapping("submit")
    public ResponseVO submit(@RequestBody GoodsOrderSubmitRequest request) {
        goodsOrderService.submit(request);
        return ResponseVO.ok();
    }

    @PutMapping("evaluate/{id}")
    public ResponseVO evaluate(@PathVariable("id") Integer id, @RequestBody GoodsOrder entity) {
        goodsOrderService.evaluate(id, entity);
        return ResponseVO.ok();
    }

    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        goodsOrderService.removeByIds(ids);
        return ResponseVO.ok();
    }
}

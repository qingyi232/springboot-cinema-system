package com.project.platform.controller;

import com.project.platform.entity.MovieOrder;
import com.project.platform.service.MovieOrderService;
import com.project.platform.vo.MovieOrderSubmitRequest;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 电影票订单
 */
@RestController
@RequestMapping("/movieOrder")
public class MovieOrderController {
    @Resource
    private MovieOrderService movieOrderService;

    @GetMapping("page")
    public ResponseVO<PageVO<MovieOrder>> page(@RequestParam Map<String, Object> query,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<MovieOrder> page = movieOrderService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    @GetMapping("selectById/{id}")
    public ResponseVO<MovieOrder> selectById(@PathVariable("id") Integer id) {
        MovieOrder entity = movieOrderService.selectById(id);
        return ResponseVO.ok(entity);
    }

    @GetMapping("list")
    public ResponseVO<List<MovieOrder>> list() {
        return ResponseVO.ok(movieOrderService.list());
    }

    @GetMapping("soldSeats/{screeningPlanId}")
    public ResponseVO<List<MovieOrder>> listSoldSeats(@PathVariable("screeningPlanId") Integer screeningPlanId) {
        return ResponseVO.ok(movieOrderService.listSoldSeats(screeningPlanId));
    }

    @PostMapping("add")
    public ResponseVO add(@RequestBody MovieOrder entity) {
        movieOrderService.insert(entity);
        return ResponseVO.ok();
    }

    @PostMapping("batch")
    public ResponseVO addBatch(@RequestBody List<MovieOrder> entityList) {
        for (MovieOrder entity : entityList) {
            movieOrderService.insert(entity);
        }
        return ResponseVO.ok();
    }

    @PostMapping("reserve")
    public ResponseVO reserve(@RequestBody MovieOrderSubmitRequest request) {
        movieOrderService.reserve(request);
        return ResponseVO.ok();
    }

    @PostMapping("submit")
    public ResponseVO submit(@RequestBody MovieOrderSubmitRequest request) {
        movieOrderService.submit(request);
        return ResponseVO.ok();
    }

    @PutMapping("update")
    public ResponseVO update(@RequestBody MovieOrder entity) {
        movieOrderService.updateById(entity);
        return ResponseVO.ok();
    }

    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids) {
        movieOrderService.removeByIds(ids);
        return ResponseVO.ok();
    }

    @PostMapping("pay/{id}")
    public ResponseVO pay(@PathVariable("id") Integer id,
                          @RequestBody(required = false) Map<String, Object> body,
                          @RequestParam(value = "paymentMethod", required = false) String paymentMethod) {
        if ((paymentMethod == null || paymentMethod.trim().isEmpty()) && body != null && body.get("paymentMethod") != null) {
            paymentMethod = String.valueOf(body.get("paymentMethod"));
        }
        movieOrderService.pay(id, paymentMethod);
        return ResponseVO.ok();
    }

    @PostMapping("cancel/{id}")
    public ResponseVO cancel(@PathVariable("id") Integer id) {
        movieOrderService.cancel(id);
        return ResponseVO.ok();
    }

    @PostMapping("confirm/{id}")
    public ResponseVO confirm(@PathVariable("id") Integer id) {
        movieOrderService.confirm(id);
        return ResponseVO.ok();
    }

    @PostMapping("verifyByCode")
    public ResponseVO<MovieOrder> verifyByCode(@RequestBody Map<String, Object> body) {
        String ticketCode = body == null || body.get("ticketCode") == null ? null : String.valueOf(body.get("ticketCode"));
        return ResponseVO.ok(movieOrderService.verifyByCode(ticketCode));
    }
}

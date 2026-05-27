package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Goods;
import com.project.platform.entity.GoodsOrder;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.GoodsMapper;
import com.project.platform.mapper.GoodsOrderMapper;
import com.project.platform.service.GoodsOrderService;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.GoodsOrderSubmitRequest;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class GoodsOrderServiceImpl implements GoodsOrderService {
    private static final Set<String> ALLOWED_PAYMENT_METHODS = Set.of("支付宝", "微信", "银行卡");

    @Resource
    private GoodsOrderMapper goodsOrderMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private UserService userService;

    @Override
    public PageVO<GoodsOrder> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<GoodsOrder> page = new PageVO<>();
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getType(), "USER")) {
            query.put("userId", currentUser.getId());
        }
        List<GoodsOrder> list = goodsOrderMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(goodsOrderMapper.queryCount(query));
        return page;
    }

    @Override
    public GoodsOrder selectById(Integer id) {
        GoodsOrder order = goodsOrderMapper.selectById(id);
        if (order == null) {
            throw new CustomException("卖品订单不存在");
        }
        return order;
    }

    @Override
    public List<GoodsOrder> list() {
        return goodsOrderMapper.list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(GoodsOrderSubmitRequest request) {
        Integer userId = assertCurrentUserIsUser();
        validateRequest(request);

        Goods goods = goodsMapper.selectById(request.getGoodsId());
        if (goods == null) {
            throw new CustomException("商品不存在");
        }
        if (!Objects.equals(goods.getStatus(), "上架")) {
            throw new CustomException("商品已下架，暂不可购买");
        }
        if (goods.getStock() == null || goods.getStock() < request.getQuantity()) {
            throw new CustomException("库存不足，请调整购买数量");
        }

        Float totalPrice = goods.getPrice() * request.getQuantity();
        userService.consumption(userId, totalPrice);

        goods.setStock(goods.getStock() - request.getQuantity());
        goodsMapper.updateById(goods);

        GoodsOrder order = new GoodsOrder();
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getName());
        order.setGoodsMainImg(goods.getMainImg());
        order.setUnitPrice(goods.getPrice());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("已完成");
        order.setUserId(userId);
        goodsOrderMapper.insert(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluate(Integer id, GoodsOrder entity) {
        Integer userId = assertCurrentUserIsUser();
        if (id == null) {
            throw new CustomException("订单不能为空");
        }
        GoodsOrder order = goodsOrderMapper.selectById(id);
        if (order == null) {
            throw new CustomException("卖品订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new CustomException("无权评价该订单");
        }
        if (!Objects.equals(order.getStatus(), "已完成")) {
            throw new CustomException("仅已完成订单可评价");
        }
        if (order.getEvaluateTime() != null) {
            throw new CustomException("该订单已评价");
        }
        if (entity == null || entity.getEvaluateRate() == null) {
            throw new CustomException("请先填写评分");
        }
        if (entity.getEvaluateRate() < 1 || entity.getEvaluateRate() > 5) {
            throw new CustomException("评分范围为1到5分");
        }
        String content = entity.getEvaluateContent() == null ? "" : entity.getEvaluateContent().trim();
        if (content.isEmpty()) {
            content = "好评";
        }
        if (content.length() > 200) {
            throw new CustomException("评价内容不能超过200字");
        }
        order.setEvaluateRate(entity.getEvaluateRate());
        order.setEvaluateContent(content);
        order.setEvaluateTime(LocalDateTime.now());
        goodsOrderMapper.updateById(order);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        if (Objects.equals(currentUser.getType(), "ADMIN")) {
            goodsOrderMapper.removeByIds(ids);
            return;
        }
        if (!Objects.equals(currentUser.getType(), "USER")) {
            throw new CustomException("当前账号无权删除卖品订单");
        }
        for (Integer id : ids) {
            GoodsOrder order = goodsOrderMapper.selectById(id);
            if (order == null) {
                continue;
            }
            if (!Objects.equals(order.getUserId(), currentUser.getId())) {
                throw new CustomException("无权删除其他用户订单");
            }
        }
        goodsOrderMapper.removeByIds(ids);
    }

    private Integer assertCurrentUserIsUser() {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        if (!Objects.equals(currentUser.getType(), "USER")) {
            throw new CustomException("只有普通用户可以购买卖品");
        }
        return currentUser.getId();
    }

    private void validateRequest(GoodsOrderSubmitRequest request) {
        if (request == null) {
            throw new CustomException("下单信息不能为空");
        }
        if (request.getGoodsId() == null) {
            throw new CustomException("商品不能为空");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new CustomException("购买数量必须大于0");
        }
        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            throw new CustomException("请选择支付方式");
        }
        if (!ALLOWED_PAYMENT_METHODS.contains(request.getPaymentMethod())) {
            throw new CustomException("支付方式不支持");
        }
    }

}

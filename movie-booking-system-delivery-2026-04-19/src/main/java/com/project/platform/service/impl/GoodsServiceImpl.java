package com.project.platform.service.impl;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.entity.Goods;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.GoodsMapper;
import com.project.platform.service.GoodsService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public PageVO<Goods> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Goods> page = new PageVO<>();
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getType(), "USER")) {
            query.put("status", "上架");
        }
        List<Goods> list = goodsMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(goodsMapper.queryCount(query));
        return page;
    }

    @Override
    public Goods selectById(Integer id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            throw new CustomException("商品不存在");
        }
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getType(), "USER") && !Objects.equals(goods.getStatus(), "上架")) {
            throw new CustomException("商品已下架");
        }
        return goods;
    }

    @Override
    public List<Goods> list() {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser != null && Objects.equals(currentUser.getType(), "USER")) {
            return goodsMapper.queryPage(0, 1000, Map.of("status", "上架"));
        }
        return goodsMapper.list();
    }

    @Override
    public void insert(Goods entity) {
        assertAdmin();
        validateGoods(entity);
        goodsMapper.insert(entity);
    }

    @Override
    public void updateById(Goods entity) {
        assertAdmin();
        if (entity.getId() == null) {
            throw new CustomException("商品ID不能为空");
        }
        validateGoods(entity);
        goodsMapper.updateById(entity);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        assertAdmin();
        goodsMapper.removeByIds(ids);
    }

    private void validateGoods(Goods entity) {
        if (entity == null) {
            throw new CustomException("商品信息不能为空");
        }
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new CustomException("商品名称不能为空");
        }
        if (entity.getCategory() == null || entity.getCategory().trim().isEmpty()) {
            throw new CustomException("商品分类不能为空");
        }
        if (entity.getPrice() == null || entity.getPrice() <= 0) {
            throw new CustomException("商品价格必须大于0");
        }
        if (entity.getStock() == null || entity.getStock() < 0) {
            throw new CustomException("商品库存不能小于0");
        }
        if (entity.getStatus() == null || entity.getStatus().trim().isEmpty()) {
            entity.setStatus("上架");
        }
    }

    private void assertAdmin() {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        if (!Objects.equals(currentUser.getType(), "ADMIN")) {
            throw new CustomException("只有管理员可以维护卖品商品");
        }
    }
}

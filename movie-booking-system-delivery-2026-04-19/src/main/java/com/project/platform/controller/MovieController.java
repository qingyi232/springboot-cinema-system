package com.project.platform.controller;

import com.project.platform.entity.Movie;
import com.project.platform.entity.MovieCollect;
import com.project.platform.mapper.MovieCollectMapper;
import com.project.platform.mapper.MovieMapper;
import com.project.platform.service.MovieService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 电影信息
 */
@RestController
@RequestMapping("/movie")
public class MovieController {
    @Resource
    private MovieService movieService;

    @Resource
    private MovieCollectMapper movieCollectMapper;

    /**
     * 分页查询
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public ResponseVO<PageVO<Movie>> page(@RequestParam Map<String, Object> query, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Movie> page = movieService.page(query, pageNum, pageSize);
        fillMovieCollectId(page.getList());
        return ResponseVO.ok(page);

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<Movie> selectById(@PathVariable("id") Integer id) {
        Movie entity = movieService.selectById(id);
        if (CurrentUserThreadLocal.getCurrentUser() != null
                && CurrentUserThreadLocal.getCurrentUser().getType().equals("USER")) {
            MovieCollect movieCollect = movieCollectMapper.selectByMovieIdAndUserId(id, CurrentUserThreadLocal.getCurrentUser().getId());
            if (movieCollect != null) {
                entity.setMovieCollectId(movieCollect.getId());
            }
        }
        return ResponseVO.ok(entity);
    }


    /**
     * 列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseVO<List<Movie>> list() {
        List<Movie> list = movieService.list();
        fillMovieCollectId(list);
        return ResponseVO.ok(list);
    }


    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody Movie entity) {
        movieService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody Movie entity) {
        movieService.updateById(entity);
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
        movieService.removeByIds(ids);
        return ResponseVO.ok();
    }

    @GetMapping("boxOfficeTop/{size}")
    public ResponseVO<List<Movie>> boxOfficeTop(@PathVariable int size) {
        return ResponseVO.ok(movieService.boxOfficeTop(size));
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("recommend/{size}")
    public ResponseVO<List<Movie>> recommend(@PathVariable int size) {
        List<Movie> list = movieService.recommend(size);
        fillMovieCollectId(list);
        return ResponseVO.ok(list);
    }

    private void fillMovieCollectId(List<Movie> movieList) {
        if (movieList == null || movieList.isEmpty()) {
            return;
        }
        if (CurrentUserThreadLocal.getCurrentUser() == null
                || !"USER".equals(CurrentUserThreadLocal.getCurrentUser().getType())) {
            return;
        }
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();
        for (Movie movie : movieList) {
            MovieCollect movieCollect = movieCollectMapper.selectByMovieIdAndUserId(movie.getId(), userId);
            if (movieCollect != null) {
                movie.setMovieCollectId(movieCollect.getId());
            }
        }
    }

}

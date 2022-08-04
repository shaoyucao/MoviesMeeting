package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmServiceApi.class, check = false)
    private FilmServiceApi filmServiceApi;

    /**
     * 获取首页信息接口
     * Api 网关：
     *     1.功能聚合【api聚合】
     *     好处：
     *          1. 六个接口，一次请求，同一时刻节省了五次Http请求
     *          2. 同一接口对外暴露，降低了前后端分离开发的难度和复杂度
     *     坏处：
     *          1. 一次获取数据过多，容易出现问题
     * @return
     */
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();

        //获取Banner信息（4到5个图）
        filmIndexVO.setBanners(filmServiceApi.getBanners());

        //获取正在热映的电影
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true, 8));

        //即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true, 8));

        //票房排行榜
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());

        //获取受欢迎的榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());

        //获取前100
        filmIndexVO.setTop100(filmServiceApi.getTop());

        return ResponseVO.success(IMG_PRE, filmIndexVO);
    }

    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(name = "catId", required = false, defaultValue = "99")String catId,
                                       @RequestParam(name = "sourceId", required = false, defaultValue = "99")String sourceId,
                                       @RequestParam(name = "yearId", required = false, defaultValue = "99")String yearId) {

        FilmConditionVO filmConditionVO = new FilmConditionVO();
        //标识位
        boolean flag = false;
        //类型集合
        List<CatVO> cats = filmServiceApi.getCats();
        List<CatVO> catResult = new ArrayList<>();
        CatVO cat = null;
        for(CatVO catVO : cats) {
            //判断集合是否存在CatId，如果存在，则将对应的实体变成active状态
            //唯一需要对99的情况额外进行判断
            if(catVO.getCatId().equals("99")) {
                cat = catVO;
                continue;
            }
            if(catVO.getCatId().equals(catId)) {
                flag = true;
                catVO.setActive(true);
            }
            else {
                catVO.setActive(false);
            }
            catResult.add(catVO);
        }
        //如果不存在，则默认将全部变为Active状态
        if(!flag) {
            cat.setActive(true);
            catResult.add(cat);
        }
        else {
            cat.setActive(false);
            catResult.add(cat);
        }

        //片源集合
        flag = false;
        List<SourceVO> sources = filmServiceApi.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        SourceVO sourceVO = null;
        for(SourceVO source : sources) {
            //判断集合是否存在CatId，如果存在，则将对应的实体变成active状态
            if(source.getSourceId().equals("99")) {
                sourceVO = source;
                continue;
            }
            if(source.getSourceId().equals(sourceId)) {
                flag = true;
                source.setActive(true);
            }
            else {
                source.setActive(false);
            }
            sourceResult.add(source);
        }
        //如果不存在，则默认将全部变为Active状态
        if(!flag) {
            sourceVO.setActive(true);
            sourceResult.add(sourceVO);
        }
        else {
            sourceVO.setActive(false);
            sourceResult.add(sourceVO);
        }

        //年代集合
        flag = false;
        List<YearVO> years = filmServiceApi.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        YearVO yearVO = null;
        for(YearVO year : years) {
            //判断集合是否存在CatId，如果存在，则将对应的实体变成active状态
            if(year.getYearId().equals("99")) {
                yearVO = year;
                continue;
            }
            if(year.getYearId().equals(yearId)) {
                flag = true;
                year.setActive(true);
            }
            else {
                year.setActive(false);
            }
            yearResult.add(year);
        }
        //如果不存在，则默认将全部变为Active状态
        if(!flag) {
            yearVO.setActive(true);
            yearResult.add(yearVO);
        }
        else {
            yearVO.setActive(false);
            yearResult.add(yearVO);
        }

        filmConditionVO.setCatInfo(catResult);

        filmConditionVO.setSourceInfo(sourceResult);

        filmConditionVO.setYearInfo(yearResult);

        return ResponseVO.success(filmConditionVO);
    }

}
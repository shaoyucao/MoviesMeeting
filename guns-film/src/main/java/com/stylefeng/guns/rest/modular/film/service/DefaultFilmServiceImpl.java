package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocBannerTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocBannerT;
import com.stylefeng.guns.rest.common.persistence.model.MoocFilmT;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = FilmServiceApi.class)
public class DefaultFilmServiceImpl implements FilmServiceApi {

    @Resource
    private MoocBannerTMapper moocBannerTMapper;

    @Resource
    private MoocFilmTMapper moocFilmTMapper;

    @Override
    public List<BannerVO> getBanners() {
        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);
        List<BannerVO> result = new ArrayList<>();

        for(MoocBannerT moocBannerT : moocBanners) {
            BannerVO bannerVO = new BannerVO();
            bannerVO.setBannerId(moocBannerT.getUuid()+"");
            bannerVO.setBannerUrl(moocBannerT.getBannerUrl());
            bannerVO.setBannerAddress(moocBannerT.getBannerAddress());
            result.add(bannerVO);
        }

        return result;
    }

    private List<FilmInfo> getFilmInfos(List<MoocFilmT> moocFilms) {
        List<FilmInfo> filmInfos = new ArrayList<>();
        for(MoocFilmT moocFilmT : moocFilms) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setScore(moocFilmT.getFilmScore());
            filmInfo.setImgAddress(moocFilmT.getImgAddress());
            filmInfo.setFilmType(moocFilmT.getFilmType());
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmId(moocFilmT.getUuid()+"");
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));

            filmInfos.add(filmInfo);
        }
        return filmInfos;
    }

    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();
        //判断是否是首页需要的内容
        //热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");
        if(isLimit) {
            //如果是，则限制条数、限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(1, nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

            //组织filmInfos
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
        }
        else {
            //如果不是，则是列表业，同样需要限制内容为热映影片

        }
        return null;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfos = new ArrayList<>();
        //判断是否是首页需要的内容
        //热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");
        if(isLimit) {
            //如果是，则限制条数、限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(1, nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

            //组织filmInfos
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
        }
        else {
            //如果不是，则是列表业，同样需要限制内容为热映影片

        }
        return null;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        return null;
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        return null;
    }

    @Override
    public List<FilmInfo> getTop() {
        return null;
    }
}

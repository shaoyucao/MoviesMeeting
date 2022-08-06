package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaServiceAPI.class)
public class DefaultCinemaServiceImpl implements CinemaServiceAPI {

    @Resource
    private MoocCinemaTMapper moocCinemaTMapper;

    @Resource
    private MoocAreaDictTMapper moocAreaDictTMapper;

    @Resource
    private MoocBrandDictTMapper moocBrandDictTMapper;

    @Resource
    private MoocHallDictTMapper moocHallDictTMapper;

    @Resource
    private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;

    @Resource
    private MoocFieldTMapper moocFieldTMapper;


    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {


        List<CinemaVO> cinemas = new ArrayList<>();
        Page<MoocCinemaT> page = new Page<>(cinemaQueryVO.getNowPage(), cinemaQueryVO.getPageSize());
        //判断是否传入查询条件(99则是全部）
        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if(cinemaQueryVO.getBrandId() != null && cinemaQueryVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaQueryVO.getBrandId());
        }
        if(cinemaQueryVO.getDistrictId() != null && cinemaQueryVO.getDistrictId() != 99) {
            entityWrapper.eq("area_id", cinemaQueryVO.getDistrictId());
        }
        if(cinemaQueryVO.getHallType() != null && cinemaQueryVO.getHallType() != 99) {
            entityWrapper.like("all_ids", "%#+"+cinemaQueryVO.getHallType()+"+#%");
        }
        List<MoocCinemaT> moocCinemaTS = moocCinemaTMapper.selectPage(page, entityWrapper);

        //将数据实体转换为业务实体
        for(MoocCinemaT moocCinemaT : moocCinemaTS) {
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(moocCinemaT.getUuid()+"");
            cinemaVO.setCinemaName(moocCinemaT.getCinemaName());
            cinemaVO.setAddress(moocCinemaT.getCinemaAddress());
            cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice()+"");
            cinemas.add(cinemaVO);
        }

        //根据条件判断影院列表总数
        long counts = moocCinemaTMapper.selectCount(entityWrapper);
        Page<CinemaVO> result = new Page<>();
        result.setRecords(cinemas);
        result.setSize(cinemaQueryVO.getPageSize());
        result.setTotal(counts);

        return result;
    }


    @Override
    public List<BrandVO> getBrands(int brandId) {
        boolean flag = false;
        List<BrandVO> brandVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);
        // 判断brandId 是否等于 99
        if(brandId == 99 || moocBrandDictT==null || moocBrandDictT.getUuid() == null){
            flag = true;
        }
        // 查询所有列表
        List<MoocBrandDictT> moocBrandDictTS = moocBrandDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for(MoocBrandDictT brand : moocBrandDictTS){
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid()+"");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if(flag){
                if(brand.getUuid() == 99){
                    brandVO.setActive(true);
                }
            }else{
                if(brand.getUuid() == brandId){
                    brandVO.setActive(true);
                }
            }
            brandVOS.add(brandVO);
        }
        return brandVOS;
    }

    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag = false;
        List<AreaVO> areaVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);
        // 判断brandId 是否等于 99
        if(areaId == 99 || moocAreaDictT==null || moocAreaDictT.getUuid() == null){
            flag = true;
        }
        // 查询所有列表
        List<MoocAreaDictT> moocAreaDictTS = moocAreaDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for(MoocAreaDictT area : moocAreaDictTS){
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid()+"");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if(flag){
                if(area.getUuid() == 99){
                    areaVO.setActive(true);
                }
            }else{
                if(area.getUuid() == areaId){
                    areaVO.setActive(true);
                }
            }

            areaVOS.add(areaVO);
        }

        return areaVOS;
    }

    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag = false;
        List<HallTypeVO> hallTypeVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallType);
        // 判断brandId 是否等于 99
        if(hallType == 99 || moocHallDictT==null || moocHallDictT.getUuid() == null){
            flag = true;
        }
        // 查询所有列表
        List<MoocHallDictT> moocHallDictTS = moocHallDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for(MoocHallDictT hall : moocHallDictTS){
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHalltypeName(hall.getShowName());
            hallTypeVO.setHalltypeId(hall.getUuid()+"");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if(flag){
                if(hall.getUuid() == 99){
                    hallTypeVO.setActive(true);
                }
            }else{
                if(hall.getUuid() == hallType){
                    hallTypeVO.setActive(true);
                }
            }

            hallTypeVOS.add(hallTypeVO);
        }

        return hallTypeVOS;
    }

    //5、根据影院编号，获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {
        MoocCinemaT moocCinemaT = moocCinemaTMapper.selectById(cinemaId);
        if(moocCinemaT == null){
            return new CinemaInfoVO();
        }
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(moocCinemaT.getUuid()+"");
        cinemaInfoVO.setCinemaId(moocCinemaT.getCinemaAddress());

        return cinemaInfoVO;
    }

    //6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {
        List<FilmInfoVO> filmInfos = moocFieldTMapper.getFilmInfos(cinemaId);
        return filmInfos;
    }

    //7、根据放映场次ID获取放映信息
    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {
        HallInfoVO hallInfoVO = moocFieldTMapper.getHallInfo(fieldId);

        return hallInfoVO;
    }

    //8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        FilmInfoVO filmInfoVO = moocFieldTMapper.getFilmInfoById(fieldId);

        return filmInfoVO;
    }

    @Override
    public OrderQueryVO getOrderNeeds(int fieldId) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();

        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);

        orderQueryVO.setCinemaId(moocFieldT.getCinemaId()+"");
        orderQueryVO.setFilmPrice(moocFieldT.getPrice()+"");

        return orderQueryVO;

    }
}

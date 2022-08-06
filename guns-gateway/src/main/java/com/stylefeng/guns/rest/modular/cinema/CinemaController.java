package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = CinemaServiceAPI.class, cache = "lru", check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @Reference(interfaceClass = OrderServiceAPI.class, check = false)
    private OrderServiceAPI orderServiceAPI;

    /**
     * 查询影院列表
     * @param cinemaQueryVO
     * @return
     */
    @RequestMapping(value = "getCinemas")
    public ResponseVO getCinemas(CinemaQueryVO cinemaQueryVO) {

        try {
            //根据条件进行查询
            Page<CinemaVO> cinemas = cinemaServiceAPI.getCinemas(cinemaQueryVO);
            //查询结果是否为空
            if(cinemas.getRecords() == null || cinemas.getRecords().size() == 0) {
                return ResponseVO.success("没有影院可查");
            }
            else {
                return ResponseVO.success(cinemas.getCurrent(), (int)cinemas.getPages(), "", cinemas.getRecords());
            }

        }catch (Exception e) {
            log.error("获取影院列表异常", e);
            return ResponseVO.serviceFail("查询影院列表失败");
        }
    }

    /**
     * 获取影院列表的查询条件
     * 1. 热点数据->放缓存
     * 2. banner
     *
     * @param cinemaQueryVO
     * @return
     */
    @RequestMapping(value = "getCondition")
    public ResponseVO getCondition(CinemaQueryVO cinemaQueryVO) {
        try {
            //获取三个集合，然后封装成一个对象返回
            List<BrandVO> brands = cinemaServiceAPI.getBrands(cinemaQueryVO.getBrandId());
            List<AreaVO> areas = cinemaServiceAPI.getAreas(cinemaQueryVO.getDistrictId());
            List<HallTypeVO> hallTypes = cinemaServiceAPI.getHallTypes(cinemaQueryVO.getHallType());

            CinemaConditionResponseVO cinemaConditionResponseVO = new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setBrandList(brands);
            cinemaConditionResponseVO.setAreaList(areas);
            cinemaConditionResponseVO.setHalltypeList(hallTypes);

            return ResponseVO.success(cinemaConditionResponseVO);
        }
        catch (Exception e) {
            log.error("获取条件列表失败", e);
            return ResponseVO.serviceFail("获取影院查询条件失败");
        }
    }

    /**
     * 获取影院的播放场次
     * @param cinemaId
     * @return
     */
    @RequestMapping(value = "getFields")
    public ResponseVO getFields(Integer cinemaId) {
        try{

            CinemaInfoVO cinemaInfoById = cinemaServiceAPI.getCinemaInfoById(cinemaId);

            List<FilmInfoVO> filmInfoByCinemaId = cinemaServiceAPI.getFilmInfoByCinemaId(cinemaId);

            CinemaFieldsResponseVO cinemaFieldResponseVO = new CinemaFieldsResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmList(filmInfoByCinemaId);

            return ResponseVO.success(IMG_PRE,cinemaFieldResponseVO);
        }catch (Exception e){
            log.error("获取播放场次失败",e);
            return ResponseVO.serviceFail("获取播放场次失败");
        }
    }

    /**
     * 获取场次详细信息
     * @param cinemaId
     * @param fieldId
     * @return
     */
    @RequestMapping(value = "getFieldInfo", method = RequestMethod.POST)
    public ResponseVO getFieldInfo(Integer cinemaId, Integer fieldId) {
        try {

            CinemaInfoVO cinemaInfoById = cinemaServiceAPI.getCinemaInfoById(cinemaId);
            FilmInfoVO filmInfoByFieldId = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
            HallInfoVO filmFieldInfo = cinemaServiceAPI.getFilmFieldInfo(fieldId);


            String soldSeatsByFieldId = orderServiceAPI.getSoldSeatsByFieldId(fieldId);
            filmFieldInfo.setSoldSeats(soldSeatsByFieldId);

            CinemaFieldResponseVO cinemaFieldResponseVO = new CinemaFieldResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmInfo(filmInfoByFieldId);
            cinemaFieldResponseVO.setHallInfo(filmFieldInfo);

            return ResponseVO.success(IMG_PRE,cinemaFieldResponseVO);
        }catch (Exception e){
            log.error("获取选座信息失败",e);
            return ResponseVO.serviceFail("获取选座信息失败");
        }
    }
}

package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.CinemaQueryVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @Reference(interfaceClass = CinemaServiceAPI.class, check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    /**
     * 查询影院列表
     * @param cinemaQueryVO
     * @return
     */
    @RequestMapping(value = "getCinemas")
    public ResponseVO getCinemas(CinemaQueryVO cinemaQueryVO) {

        return null;
    }

    /**
     * 获取影院列表的查询条件
     * @param cinemaQueryVO
     * @return
     */
    @RequestMapping(value = "getCondition")
    public ResponseVO getCondition(CinemaQueryVO cinemaQueryVO) {


        return null;
    }

    /**
     * 获取影院的播放场次
     * @param cinemaId
     * @return
     */
    @RequestMapping(value = "getFields")
    public ResponseVO getFields(Integer cinemaId) {


        return null;
    }

    /**
     * 获取场次详细信息
     * @param cinemaId
     * @param fieldId
     * @return
     */
    @RequestMapping(value = "getFieldInfo", method = RequestMethod.POST)
    public ResponseVO getFieldInfo(Integer cinemaId, Integer fieldId) {


        return null;
    }
}

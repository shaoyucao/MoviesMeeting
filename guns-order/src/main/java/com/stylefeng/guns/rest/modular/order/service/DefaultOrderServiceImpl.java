package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Service(interfaceClass = OrderServiceAPI.class)
public class DefaultOrderServiceImpl implements OrderServiceAPI {

    @Resource
    private MoocOrderTMapper moocOrderTMapper;

    @Resource
    private FTPUtil ftpUtil;

    //验证是否为真实的座位
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        //根据FieldId找到对应的座位位置图
        String seatPath = moocOrderTMapper.getSeatsByFieldId(fieldId);

        //读取位置图，判断seats是否为真
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);

        //将fileStrByAddress转换为json对象
        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);
        // seats = 1,2,3 ids = "1, 2, 3, 6, 88"，判断seats是否在ids中有
        String ids = jsonObject.get("ids").toString();

        // 每一次匹配上的，都给isTrue+1
        String[] seatArrs = seats.split(",");
        String[] idArrs = ids.split(","); //位置图的id
        int isTrue = 0;
        for(String id : idArrs){
            for(String seat : seatArrs){
                if(seat.equalsIgnoreCase(id)){
                    isTrue++;
                }
            }
        }

        // 如果匹配上的数量与已售座位数一致，则表示全都匹配上了
        if(seatArrs.length == isTrue){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        return false;
    }

    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        return null;
    }

    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {
        return null;
    }

    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        return null;
    }
}

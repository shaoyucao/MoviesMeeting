package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value="/order/")
public class OrderController {

    @Reference(interfaceClass = OrderServiceAPI.class,
            check = false,
            group = "order2018"
    )
    private OrderServiceAPI orderServiceAPI;

    @Reference(interfaceClass = OrderServiceAPI.class,
            check = false,
            group = "order2017"
    )
    private OrderServiceAPI orderServiceAPI2017;

    //购票
    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId, String soldSeats, String seatsName) {

            // 验证售出的票是否为真(需要开启ftp服务器文件验证）-->将座位号去json文件中对比一下，看下是否在里面
            boolean isTrue = orderServiceAPI.isTrueSeats(fieldId+"",soldSeats);

            // 已经销售的座位里，有没有这些座位
            boolean isNotSold = orderServiceAPI.isNotSoldSeats(fieldId+"",soldSeats);

            // 验证，上述两个内容有一个不为真，则不创建订单信息
            if(isTrue && isNotSold){
                // 创建订单信息,注意获取登陆人
                String userId = CurrentUser.getCurrentUser();
                if(userId == null || userId.trim().length() == 0){
                    return ResponseVO.serviceFail("用户未登陆");
                }
                OrderVO orderVO = orderServiceAPI.saveOrderInfo(fieldId,soldSeats,seatsName,Integer.parseInt(userId));
                if(orderVO == null){
                    log.error("购票未成功");
                    return ResponseVO.serviceFail("购票业务异常");
                }else{
                    return ResponseVO.success(orderVO);
                }
            }else{
                return ResponseVO.serviceFail("订单中的座位编号有问题");
            }
    }

    //获取当前登录人的购票信息
    @RequestMapping(value = "getOrderInfo", method = RequestMethod.POST)
    public ResponseVO getOrderInfo(@RequestParam(value = "nowPage", required = false, defaultValue = "1")Integer nowPage,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "5")Integer pageSize
    ) {

        // 获取当前登陆人的信息
        String userId = CurrentUser.getCurrentUser();

        // 使用当前登陆人获取已经购买的订单
        Page<OrderVO> page = new Page<>(nowPage,pageSize);
        if(userId != null && userId.trim().length()>0){
            Page<OrderVO> result = orderServiceAPI.getOrderByUserId(Integer.parseInt(userId), page);

            Page<OrderVO> result2017 = orderServiceAPI2017.getOrderByUserId(Integer.parseInt(userId), page);

            log.warn(result.getRecords()+""+result2017.getRecords());
            // 合并结果
            int totalPages = (int)(result.getPages() + result2017.getPages());
            // 2017和2018的订单总数合并
            List<OrderVO> orderVOList = new ArrayList<>();
            orderVOList.addAll(result.getRecords());
            orderVOList.addAll(result2017.getRecords());

            return ResponseVO.success(nowPage,totalPages,"",orderVOList);

        }else{
            return ResponseVO.serviceFail("用户未登陆");
        }
    }
}

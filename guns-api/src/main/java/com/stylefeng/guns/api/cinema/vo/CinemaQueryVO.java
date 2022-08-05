package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaQueryVO implements Serializable {

    //影院编号
    private Integer brandId=99;
    //行政区编号
    private Integer districtId=99;
    //影厅类型
    private Integer hallType=99;
    private Integer pageSize=12;
    private Integer nowPage=1;

}

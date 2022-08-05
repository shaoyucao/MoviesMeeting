package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmDetailVO implements Serializable {
    private String filmId;
    private String filmname;
    private String filmEnName;
    private String imgAddress;
    private String scoreNum;
    private String totalBox;
    //影片类型
    private String info01;
    //影片来源+时长
    private String info02;
    //上映时间
    private String info03;
    //影片详细信息
    private InfoRequestVO info04;
}

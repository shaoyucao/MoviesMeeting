package com.stylefeng.guns.rest.modular.vo;

import lombok.Data;

@Data
public class ResponseVO<M> {

    //返回状态（0-成功，1-业务失败，999-系统异常）
    private int status;

    //返回的错误信息
    private String msg;

    //返回的数据实体
    private M data;

    //图片前缀
    private String imgPre;

    //分页使用
    private int nowPage;
    private int totalPage;

    private ResponseVO(){}

    public static<M> ResponseVO success(String imgPre, M m) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setImgPre(imgPre);
        responseVO.setData(m);
        return responseVO;
    }

    public static<M> ResponseVO success(int nowPage, int totalPage, String imgPre, M m) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setImgPre(imgPre);
        responseVO.setData(m);
        responseVO.setNowPage(nowPage);
        responseVO.setTotalPage(totalPage);
        return responseVO;
    }

    public static<M> ResponseVO success(M m) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setData(m);
        return responseVO;
    }

    public static<M> ResponseVO success(String msg) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static<M> ResponseVO serviceFail(String msg) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(1);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static<M> ResponseVO appFail(String msg) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(999);
        responseVO.setMsg(msg);
        return responseVO;
    }


}

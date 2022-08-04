package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/")
@RestController
public class UserController {

    /**
     * 配置Dubbo的启动检查check = false， 保证即使服务端不启动，客户端也能正常启动，但调用服务的时候会报内部错误
     * @Reference(interfaceClass = UserAPI.class, check = false)
     */
    @Reference(interfaceClass = UserAPI.class, check = false)
    private UserAPI userAPI;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseVO register(UserModel userModel) {
        if(userModel.getUsername() == null || userModel.getUsername().trim().length() == 0) {
            return ResponseVO.serviceFail("用户名不能为空");
        }

        if(userModel.getPassword() == null || userModel.getPassword().trim().length() == 0) {
            return ResponseVO.serviceFail("密码不能为空");
        }

        //用户名不能重复
        //如果用户名存在
        if(!userAPI.checkUsername(userModel.getUsername())) {
            return ResponseVO.serviceFail("用户名已存在，请更换用户名后重新注册！");
        }

        boolean isSuccess = userAPI.registry(userModel);
        if(isSuccess) {
            return ResponseVO.success("注册成功");
        }
        else {
            return ResponseVO.serviceFail("注册失败");
        }
    }

    @RequestMapping(value = "check", method = RequestMethod.POST)
    public ResponseVO check(String username) {
        if(username != null && username.trim().length() > 0) {
            boolean notExists = userAPI.checkUsername(username);
            if(notExists) {
                return ResponseVO.success("用户名不存在");
            }
            else {
                return ResponseVO.serviceFail("用户名已存在");
            }
        }
        else {
            return ResponseVO.serviceFail("用户名不能为空");
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ResponseVO logout() {
        /**
         * 应用：
         * 1. 前端存储JWT【七天】，JWT的刷新
         * 2. 服务器端会存储活动用户信息【30分钟】
         * 3. JWT里的userId为key, 查找活跃用户
         *
         * 退出：
         * 1. 前端删除JWT
         * 2. 后端服务器删除活跃用户缓存
         *
         * 现状：
         * 1. 前端删除JWT
         *
         */
        return ResponseVO.success("用户退户成功");
    }

    @RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
    public ResponseVO getUserInfo() {
        //获取当前登录用户
        String userId = CurrentUser.getCurrentUser();
        if(null != userId || userId.trim().length() > 0) {
            //将用户id传入后端进行查询
            int uuid = Integer.parseInt(userId);
            UserInfoModel userInfo = userAPI.getUserInfo(uuid);
            if(userInfo != null) {
                return ResponseVO.success(userInfo);
            }
            else {
                return ResponseVO.appFail("用户信息查询失败");
            }
        }
        else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }


    @RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
    public ResponseVO updateUserInfo(UserInfoModel userInfoModel) {
        //获取当前登录用户
        String userId = CurrentUser.getCurrentUser();
        if(null != userId || userId.trim().length() > 0) {
            //将用户id传入后端进行查询
            int uuid = Integer.parseInt(userId);
            //判断当前登录人的ID 和修改的结果id是否一致
            if(uuid != userInfoModel.getUuid()) {
                return ResponseVO.serviceFail("请修改您个人的信息");
            }
            UserInfoModel userInfo = userAPI.updateUserInfo(userInfoModel);
            if(userInfo != null) {
                return ResponseVO.success(userInfo);
            }
            else {
                return ResponseVO.appFail("用户信息修改失败");
            }
        }
        else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }

}

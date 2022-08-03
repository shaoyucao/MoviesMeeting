package com.stylefeng.guns.api.user;

public interface UserAPI {
    int login(String username, String password);

    boolean registry(UserModel userModel);

    /**
     * 校验用户名
     * 存在返回false，不存在返回true
     * @param username
     * @return
     */
    boolean checkUsername(String username);

    UserInfoModel getUserInfo(int uuid);

    UserInfoModel updateUserInfo(UserInfoModel userInfoModel);
}

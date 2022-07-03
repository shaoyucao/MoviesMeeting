package com.stylefeng.guns.api.user;

public interface UserAPI {
    int login(String username, String password);

    boolean registry(UserModel userModel);

    boolean checkUsername(String username);

    UserInfoModel getUserInfo(int uuid);

    UserInfoModel updateUserInfo(UserInfoModel userInfoModel);
}

package service;

import com.alibaba.fastjson.JSONObject;

public interface IWeixinService {
    /**
     * 微信登陆--->跳转微信登陆的界面
     * @param code
     * @return
     */
    JSONObject getWeixinUserInfo(String code);

    /**
     * 通过 coede获得用户的token
     * @param code
     * @return
     */
    JSONObject getAccecessToken(String code);
}

package weixinserviceImpl;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import service.IWeixinService;
import utils.ShiHouApiProperties;
import utils.ThirdLoginTypeEnum;

import java.io.UnsupportedEncodingException;

@Service("weixinService")
public class WeixinserviceImpl implements IWeixinService {


    /**
     * //https://api.weixin.qq.com/sns/oauth2/access_token?
     appid=APPID&
     secret=SECRET&code=CODE&
     grant_type=authorization_code
     * 通过code获取access_token
     * @param code
     * @return
     */
    @Override
    public JSONObject getAccecessToken(String code) {
        String tokenJson= null;
        String url = String.format(weixin_open_sns_oauth2, code);
        String resultStr = restTemplate.getForObject(url, String.class);
        JSONObject result = JSONObject.parseObject(resultStr);
        if (result.getString("access_token")!= null){
            log.info(String.format("getAccessToken --> %s", result));
            return result;
        }else {
            log.error(String.format("getAccessToken------------->%s",result));
        }
        return null;
    }
    private Logger log = LogManager.getLogger(getClass());
    @Autowired
    private RestTemplate restTemplate;
    @Value("${weixin.open.sns_oauth2}")
    private String weixin_open_sns_oauth2;//通过code获取access_token
    @Value("${weixin.open.sns_userinfo}")  //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
    private String weixin_open_sns_userinfo;//根据access token 获取用户信息
    @Autowired
    private ShiHouApiProperties shiHouApiProperties;
    /**
     * 根据access_token获取用户的信息
     * @param code
     * @return
     *
     */
    @Override
        public JSONObject getWeixinUserInfo(String code) {
        JSONObject accessToken = getAccecessToken(code);
        if (accessToken.getString("access_token") == null){
            return accessToken;
        }
        try {
            //拿到access_token
            String access_token = accessToken.getString("access_token");
            String openId = accessToken.getString("openId");
            String unionid = accessToken.getString("unionid");
            String url = String.format(weixin_open_sns_userinfo,access_token, openId, unionid);
            String resultStr = restTemplate.getForObject(url, String.class);
            resultStr = new String(resultStr.getBytes("ISO-8859-1"), "UTF-8");
            JSONObject result = JSONObject.parseObject(resultStr);
            //调用第三放登陆的接口
           JSONObject thirdResult =  thirdLogin(ThirdLoginTypeEnum.WEXIN.getCode(),openId,unionid,access_token,"","");

            log.info(String.format("微信 狮吼平台返回信息 thirdResult --> %s", thirdResult.toJSONString()));

            int errorCode = thirdResult.getInteger("error");
            if(errorCode == 110020 || errorCode == 110030){
                String nickName = result.getString("nickname");
                String headimgurl = result.getString("headimgurl");
                JSONObject loginUser = thirdUserUp(ThirdLoginTypeEnum.QQ.getCode(), openId, unionid, access_token, nickName, headimgurl);
                log.info(String.format("微信第三方登陆 狮吼平台创建用户 -- %s", loginUser));
                return loginUser;
            }
            return thirdResult;
        } catch (UnsupportedEncodingException e) {
            log.error(String.format("转码错误----->%s",e.getMessage()));
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 创建用户接口
     * @param platform
     * @param openid
     * @param union_id
     * @param access_token
     * @param nick_name
     * @param avatar_url
     * @return
     */

    private JSONObject thirdUserUp(String platform, String openid, String union_id, String access_token,
                                  String nick_name, String avatar_url) {
        String url = String.format(shiHouApiProperties.getSignUp(), platform, openid, union_id, access_token, nick_name,
                avatar_url);
        log.info(String.format("thirdUserUp url --> %s", url));
        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class);
        log.info(String.format("thirdUserUp --> %s", jsonObject.toJSONString()));
        return jsonObject;
    }
    /**
     * 第三方登陆
     *
     * @param platform
     * @param openid
     * @param union_id
     * @param access_token
     * @param expire_time
     * @param raw
     * @return
     */

    private JSONObject thirdLogin(String platform, String openid, String union_id, String access_token,
                                 String expire_time, String raw) {
        String url = String.format(shiHouApiProperties.getThirdLogin(), platform, openid, union_id, access_token,
                expire_time, raw);
        log.info(String.format("thirdLogin url --> %s", url));
        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class);
        log.info(String.format("thirdLogin --> %s", jsonObject.toJSONString()));
        return jsonObject;
    }

}

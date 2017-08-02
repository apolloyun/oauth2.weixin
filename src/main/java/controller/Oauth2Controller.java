package controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.IWeixinService;
import utils.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/oauth")
public class Oauth2Controller {
    //日志
    private Logger logger = LogManager.getLogger(getClass());
    //注入
    @Autowired
    private IWeixinService weixinService;
    @Value("${weixin.open.connect}")
    private String weixin_open_connect;

@GetMapping("/weixin/login")
@ResponseBody
/**
 * 微信的登陆界面
 */
    public void weixinLogin (HttpServletRequest request, HttpServletResponse response){

        try {
            response.setContentType("text/html;charset=utf-8");//设置编码格式
            String redirect = request.getParameter("redirect");
            request.getSession().setAttribute("redirecrShihou",redirect);
            logger.info(String.format("weixinRedirect--------?%s",weixin_open_connect));
            response.sendRedirect(weixin_open_connect);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/**
 * 通过code获得用户的登陆信息
 */

public ResponseEntity<Result> getWeixinUserInfo(@RequestParam(value = "code")String code){

    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(Result.build().content(weixinService.getWeixinUserInfo(code)));
}
}

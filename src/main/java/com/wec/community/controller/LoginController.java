package com.wec.community.controller;

import com.google.code.kaptcha.Producer;
import com.wec.community.dao.LoginTicketMapper;
import com.wec.community.entity.User;
import com.wec.community.service.UserService;
import com.wec.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);//创建日志对象

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer; //注入验证码生成的bean


    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }


    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()){
            //注册成功
            model.addAttribute("msg","注册成功,我们已经向您的邮箱发送了激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code")String code){
        int result = userService.activation(userId, code);
        if (result == ACTIVSYION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用");
            model.addAttribute("target","/login");
        } else if (result == ACTIVSYION_REPEAT) {
            model.addAttribute("msg","无效操作，该账号已经激活");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，您的激活码不正确");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response,HttpSession session){//验证码为敏感数据，所以用session存储
        //生成验证码
        String text = kaptchaProducer.createText();//验证码的内容
        BufferedImage image = kaptchaProducer.createImage(text);//生成与上面对应的验证码图片
        //将验证码存入session
        session.setAttribute("kaptcha",text);
        //将图片输出到浏览器
        response.setContentType("image/png");//声明传入response的是什么类型
        try {
            OutputStream os = response.getOutputStream();//创建字节输出流
            ImageIO.write(image,"png",os);//字节输出流输出图片
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }
    }
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean remember,Model model,HttpSession session,HttpServletResponse response){
        //检查验证码
        String kaptcha= (String)session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code)|| !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMag","验证码不正确");
            return "site/login";
        }
        //验证账号和密码
        int expiredSeconds = remember ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            if (map.containsKey("usernameMag")){
                model.addAttribute("usernameMag",map.get("usernameMag").toString());
            }else {
                model.addAttribute("usernameMag",null);
            }
            if (map.containsKey("passwordMag")){
                model.addAttribute("passwordMag",map.get("passwordMag").toString());
            }else {
                model.addAttribute("passwordMag",null);
            }
            return "site/login";
        }
    }
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}

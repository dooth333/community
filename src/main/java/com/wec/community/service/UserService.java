package com.wec.community.service;

import com.wec.community.dao.LoginTicketMapper;
import com.wec.community.dao.UserMapper;
import com.wec.community.entity.LoginTicket;
import com.wec.community.entity.User;
import com.wec.community.util.CommunityConstant;
import com.wec.community.util.CommunityUtil;
import com.wec.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    //注入th模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domian}")
    private String domian;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    /***
     * 注册方法
     * @param user
     * @return
     */
    public Map<String,Object> register(User user) {
        Map<String,Object> map = new HashMap<>();
        //空值处理
        if (user == null){
            try {
                throw new IllegalAccessException("参数不能为空");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证账号
        User selectByName = userMapper.selectByName(user.getUsername());
        if (selectByName != null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        //验证邮箱
        User selectByEmail = userMapper.selectByEmail(user.getEmail());
        if (selectByEmail != null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
//        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setHeaderUrl("http://images.nowcoder.com/head/5t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domian + contextPath + "/activation/"+user.getId() + "/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活wenc账户",content);
        return map;
    }

    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVSYION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVSYION_SUCCESS;
        }else {
            return ACTIVSYION_FAILURE;
        }
    }

    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        //空值验证
        if (StringUtils.isBlank(username)){
            map.put("usernameMag","账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMag","密码不能为空！");
            return map;
        }
        //验证账号是否存在
        User user = userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMag","账号不存在！");
            return map;
        }
        //验证账号是否状态
        if (user.getStatus() == 0){
            map.put("usernameMag","账号未激活");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMag","密码错误！");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+ expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    //退出登录
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);//1表示无效
    }

    //查询登录凭证
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }
}

package com.wec.community.controller;

import com.wec.community.annotation.LoginRequired;
import com.wec.community.entity.User;
import com.wec.community.service.UserService;
import com.wec.community.util.CommunityUtil;
import com.wec.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.multi.MultiPanelUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domian}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /***
     * 上传头像，保存头像，更新用户头像路径
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    //(这里面如果用html传入的数据，在html的input属性中加入name属性并且与这里的参数名一样)
    public String uploadHeader(MultipartFile headerImage, Model model){//springMvc提供的一个专有的类型进行图片上传

        if (headerImage == null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();//读取图片原始文件名
        String suffix = fileName.substring(fileName.lastIndexOf("."));//从最后一个点截取后面的全部，得到文件后缀名
        //判断后缀名是否正确
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        //给图片随机生成图片名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest= new File(uploadPath+ "/"+ fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);//把当前文件存到目标路径
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常",e);//把异常抛出，以后处理
        }
        //更新当前用户头像路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/"+ fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }


    /***
     * 根据头像地址读取头像到网页
     * @param fileName
     * @param response
     */
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try(
                FileInputStream fis = new FileInputStream(fileName);//在try后面加一个()里面声明的变量如果自带close关闭方法，用完之后会自动创建finally进行自动关闭
                OutputStream os = response.getOutputStream();
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 修改密码
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/setPassword",method = RequestMethod.POST)
    public String setPassword(String oldPassword,String newPassword,String confirmPassword,Model model){
        User user = hostHolder.getUser();
        if (newPassword == null){
            model.addAttribute("newPasswordMag","不能为空");
            return "/site/setting";
        }
        if (newPassword == null){
            model.addAttribute("confirmPassword","不能为空");
            return "/site/setting";
        }
        if (!newPassword.equals(confirmPassword)){
            model.addAttribute("confirmPassword","两次密码不一致");
            return "/site/setting";
        }
        if ( !CommunityUtil.md5(oldPassword+user.getSalt()).equals(user.getPassword())){
            model.addAttribute("oldPasswordMag","原始密码不正确");
            return "/site/setting";
        }
        userService.setPassword(user,newPassword);
        return "redirect:/logout";
    }
}

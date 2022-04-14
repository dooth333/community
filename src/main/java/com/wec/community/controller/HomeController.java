package com.wec.community.controller;

import com.wec.community.entity.DiscussPost;
import com.wec.community.entity.Page;
import com.wec.community.entity.User;
import com.wec.community.service.DiscussPostService;
import com.wec.community.service.LikeService;
import com.wec.community.service.UserService;
import com.wec.community.util.CommunityConstant;
import com.wec.community.util.CommunityUtil;
import com.wec.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    /***
     * 主页
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用之前，SpringMVC会自动实例化Model,并将Page注入Model
        //所以，在thymeleaf中可以直接访问page对象中的数据。
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for (DiscussPost post : list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                Long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }
        User user = hostHolder.getUser();
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    /***
     * 错误提示
     * @return
     */
    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "error/500";
    }

    /***
     * 拒绝访问时的提示页面
     * @return
     */
    @RequestMapping(path = "/denied", method = {RequestMethod.GET})
    public String getDeniedPage() {
        return "/error/404";
    }
}

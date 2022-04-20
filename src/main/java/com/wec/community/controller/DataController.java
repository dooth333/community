package com.wec.community.controller;

import com.wec.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    //打卡统计页面(既可以get也可以Post请求)
    @RequestMapping(path = "/data", method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }

    //统计网站UV请求
    @RequestMapping(path = "/data/uv",method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){//传入的为字符串，但是我们可以通过注解告诉字符串格式
        long uv = dataService.calculateUV(start,end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartData",start);
        model.addAttribute("uvEndData",end);
        return "forward:/data"; //forward为我只处理一半，剩下的一般/data路径的方法处理，进行转到模板
    }


    //统计网站活跃用户
    @RequestMapping(path = "/data/dau",method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){//传入的为字符串，但是我们可以通过注解告诉字符串格式
        long dau = dataService.calculateDAU(start,end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartData",start);
        model.addAttribute("dauEndData",end);
        return "forward:/data"; //forward为我只处理一半，剩下的一般/data路径的方法处理，进行转到模板
    }

}

package com.wec.community.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wec.community.dao.AlphaDao;
import com.wec.community.service.AlphaService;
import com.wec.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+": "+ value);
        }
        System.out.println(request.getParameter("code"));

        //response,给浏览器返回响应数据
        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try (PrintWriter writer = response.getWriter();){
            writer.write("<h1>Spring</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Get请求两种传参数的方式
    // /students?current=1&limit=20
    //详细的配置
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            //若参数名与 "/students?current=1&limit=20" 中的参数值保持一致便可自动获取不再需要request.getParameter
            @RequestParam(name = "current",required = false,defaultValue = "1") int current, //required = false表示可以不传参数,defaultValue = "1"//如若不传的时的默认值
            @RequestParam(name = "limit",required = false,defaultValue = "20") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "from Students";
    }

    // /students/123  参数成为路径的一部分
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id")int id){ //通过@PathVariable注解得到
        System.out.println(id);
        return "a student";
    }

    //post请求浏览器向服务器提交数据
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){ //若参数名与提交中的参数值保持一致便可自动获取
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应Html数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET) //不加返回的注解如（@ResponseBody）默认为返回html数据
    public ModelAndView getTeacher(){ // ModelAndView返回Mode和View两样数据
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }

    //返回动态html的第二种方式
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){ //如果返回的是String表示返回的是view的html的路径
        model.addAttribute("name","郑州轻工业大学");
        model.addAttribute("age","110");
        return "/demo/view";
    }

    //响应json数据（异步请求）
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody  //写他表示返回的非html数据
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",33);
        emp.put("salary",8000.00);
        return emp;
    }
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody  //写他表示返回的非html数据
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> mapList = new ArrayList<>();
        Map<String,Object> emp1 = new HashMap<>();
        emp1.put("name","张三");
        emp1.put("age",33);
        emp1.put("salary",8000.00);
        Map<String,Object> emp2 = new HashMap<>();
        emp2.put("name","李四");
        emp2.put("age",25);
        emp2.put("salary",6000.00);
        Map<String,Object> emp3 = new HashMap<>();
        emp3.put("name","王二");
        emp3.put("age",21);
        emp3.put("salary",10000.00);
        mapList.add(emp1);
        mapList.add(emp2);
        mapList.add(emp3);
        return mapList;
    }

    // cookie示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie\
        //CommunityUtil.generateUUID()随机生成数
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie生效范围
        cookie.setPath("/community/alpha");
        //设置cookie的设置生存时间
        cookie.setMaxAge(60*10);//单位是秒
        //发送cookie
        response.addCookie(cookie);

        return "set Cookie";
    }
    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        //获取cookie
        System.out.println(code);
        return code;
    }

    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){ //不用手动创建session,声明之后自动注入
        //session可以存很多数据，不止字符串
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set Session";

    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        //获取session
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "getSession";

    }
    // ajax示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String textAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }

}

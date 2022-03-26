package com.wec.community.dao;

import com.wec.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper //声明mapper对象
@Deprecated //声明该组件不推荐使用了
public interface LoginTicketMapper {

    //增加凭证
    @Insert({//可以多个字符串拼接sql语句
            "insert into login_ticket(user_id,ticket,status,expired) ",//每一句之间留一个空格，防止拼接时连一起
            "values(#{userId},#{ticket},#{status},#{expired}) "
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")//注入主键
    int insertLoginTicket(LoginTicket loginTicket);

    //通过凭证查找
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改凭证的状态
    //注解写sql也可以写动态sql，比如if等等   (\"是对"的转义，不能直接打出)
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket}",
            "<if test=\"ticket != null\">",
            "and 1=1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);
}

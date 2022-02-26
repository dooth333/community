package com.wec.community;
import com.wec.community.dao.DiscussPostMapper;
import com.wec.community.dao.UserMapper;
import com.wec.community.entity.DiscussPost;
import com.wec.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import java.util.Date;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
    public class MapperTests {
        @Autowired
        private UserMapper userMapper;

        @Autowired
        private DiscussPostMapper discussPostMapper;

        @Test
        public void testSelectUser(){
            User user = userMapper.selectById(11);
            System.out.println(user);

            User aaa = userMapper.selectByName("aaa");
            System.out.println(aaa);

            User user1 = userMapper.selectByEmail("nowcoder22@sina.com");
            System.out.println(user1);
        }
        @Test
        public void testInsertUser(){
             User user = new User("test","123456","abc","test@qq.com",0,1,null,"http://www.nowcoder.com/111.png", new Date());
            int i = userMapper.insertUser(user);
            System.out.println(i);
        }

        @Test
        public void updateUser(){
            int row = userMapper.updateStatus(150,0);
            System.out.println(row);
            row = userMapper.updatePassword(150,"456789");
            System.out.println(row);
            row = userMapper.updateHeader(150,"http://www.nowcoder.com/222.png");
            System.out.println(row);
        }

        @Test
        public void testSelectPost(){
            List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
            for (DiscussPost post:discussPosts){
                System.out.println(post);
            }
            int rows = discussPostMapper.selectDiscussPostRows(0);
            System.out.println(rows);
        }
    }


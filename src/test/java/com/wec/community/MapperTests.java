package com.wec.community;
import com.wec.community.dao.*;
import com.wec.community.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
    public class MapperTests {
        @Autowired
        private UserMapper userMapper;

        @Autowired
        private DiscussPostMapper discussPostMapper;

        @Autowired
        private LoginTicketMapper loginTicketMapper;

        @Autowired
        private CommentMapper commentMapper;

        @Value("${community.path.upload}")
        private String uploadPath;

        @Autowired
        private MessageMapper messageMapper;

        @Test
        public void print(){
            System.out.println(uploadPath);
        }
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
            List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10,0);
            for (DiscussPost post:discussPosts){
                System.out.println(post);
            }
            int rows = discussPostMapper.selectDiscussPostRows(0);
            System.out.println(rows);
        }

        @Test
        public void testInsertLoginTicket(){
           LoginTicket loginTicket = new LoginTicket(1,123,"abc",0,new Date(System.currentTimeMillis()+1000*60*10));
           loginTicketMapper.insertLoginTicket(loginTicket);
        }
        @Test
        public void testUpdateLoginTicket(){
            LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
            System.out.println(loginTicket);
            loginTicketMapper.updateStatus("abc",1);
            loginTicket = loginTicketMapper.selectByTicket("abc");
            System.out.println(loginTicket);
        }

        @Test
        public void commentTest(){
            List<Comment> comments = commentMapper.selectCommentsByEntity(1, 234, 1, 5);
            System.out.println(comments);
            int i = commentMapper.selectCountByEntity(1, 234);
            System.out.println(i);
        }

        @Test
        public void messageTest(){
            List<Message> messagesList = messageMapper.selectConversation(111, 0, 20);
            for (Message ms : messagesList){
                System.out.println(ms);
            }
            int i = messageMapper.selectConversationCount(111);
            System.out.println(i);

            List<Message> list = messageMapper.selectLetters("111_112", 0, 10);
            list.forEach(System.out::println);
            int i1 = messageMapper.selectLetterCount("111_112");
            System.out.println(i1);
            int i2 = messageMapper.selectLetterUnreadCount(131, "111_131");
            System.out.println(i2);
        }

    @Test
    public void messageTest2(){
            List<Integer> ids = new ArrayList<>();
          messageMapper.updateStatus(ids,0);
    }
    }


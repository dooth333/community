package com.wec.community.controller;

import com.wec.community.entity.Comment;
import com.wec.community.entity.DiscussPost;
import com.wec.community.entity.Event;
import com.wec.community.event.EventProducer;
import com.wec.community.service.CommentService;
import com.wec.community.service.DiscussPostService;
import com.wec.community.util.CommunityConstant;
import com.wec.community.util.HostHolder;
import com.wec.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * 评论帖子或评论
     * @param discussPostId 被评论的id
     * @param comment 回复内容
     * @return
     */
    @RequestMapping(value = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event()
            .setTopic(TOPIC_COMMENT)
            .setUserId(hostHolder.getUser().getId())
            .setEntityType(comment.getEntityType())
            .setEntityId(comment.getEntityId())
            .setData("postId",discussPostId);

        //传入target也就是作者
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST){
            //触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
            //计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}

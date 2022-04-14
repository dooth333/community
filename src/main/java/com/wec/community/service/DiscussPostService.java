package com.wec.community.service;

import com.wec.community.dao.DiscussPostMapper;
import com.wec.community.entity.DiscussPost;
import com.wec.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if (post == null){
            try {
                throw new IllegalAccessException("参数不能为空");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //转义html标记（防止用户写的 < > 等标签破坏网页）
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * 通过id找帖子
     * @param id 帖子id
     * @return
     */
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    /***
     * 更新评论数量
     * @param id 要更新的帖子id
     * @param CommentCount  要更新的数量
     * @return
     */
    public int updateCommentCount(int id,int CommentCount){
        return discussPostMapper.updateCommentCount(id,CommentCount);
    }


    /***
     * 修改帖子类型
     * @param id 要改的帖子id
     * @param type 要改成的类型
     * @return
     */
    public int updateType(int id,int type){
        return discussPostMapper.updateType(id, type);
    }

    /***
     * 修改帖子状态
     * @param id 要改的帖子id
     * @param status 要改成的状态
     * @return
     */
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id, status);
    }


}

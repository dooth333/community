package com.wec.community.dao;

import com.wec.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit,int orderMode);

    //@Param("userId")给参数起别名
    // 动态拼SQL语句，且只有一个参数，必须取别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);

    int updateType(int id,int type);

    int updateStatus(int id, int status);

    int updateScore(int id,double score);
}

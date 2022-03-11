package com.wec.community.dao;

import com.wec.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 查询当前用户的会话列表，针对每个会话只返回一条最新私信
    List<Message> selectConversation(int userId,int offset,int limit);

    // 查询当前用户会话数量
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);

    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量
    int selectLetterUnreadCount(int userId,String conversationId);

    //增加消息
    int insertMessage(Message message);

    //更改消息的状态
    int updateStatus(List<Integer> ids , int status);
}
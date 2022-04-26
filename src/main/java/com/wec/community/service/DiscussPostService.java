package com.wec.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wec.community.dao.DiscussPostMapper;
import com.wec.community.entity.DiscussPost;
import com.wec.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.post.expire-seconds}")
    private int expireSeconds;

    //caffeine 的核心接口： Cache， LoadingCache(同步缓存，经常用), AsyncLoadingCache(异步缓存)

    //帖子列表的缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;

    //缓存帖子总数
    private LoadingCache<Integer,Integer> postRowsCache;

    //初始化方法，只在服务启动时执行一次
    @PostConstruct
    public void init(){
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize) //最大帖子缓存列表数
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS) //过期时间，单位为秒
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        //在缓存中该数据不存在时，要在这提供一个从MySQL数据库查询数据的方法
                        if (key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] params = key.split(":");
                        if (params == null || params.length != 2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        //二级缓存: 先查询Redis，如Redis没有再访问Mysql



                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0,offset, limit, 1);
                    }
                });
        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize) //最大帖子缓存列表数
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS) //过期时间，单位为秒
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {

                        logger.debug("load post Rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });

    }


    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode){
        if (userId==0 && orderMode==1){//只在首页并且查看热门贴的时候启用缓存
            return postListCache.get(offset+":"+limit);
        }
        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    public int findDiscussPostRows(int userId){
        if (userId==0){//首页查询时启用缓存
            return postRowsCache.get(userId);
        }
        logger.debug("load post Rows from DB.");
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


    /***
     * 修改帖子分数
     * @param id 要改的帖子id
     * @param score 要改成的分数
     * @return
     */
    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id, score);
    }


}

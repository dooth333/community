package com.wec.community.controller;

import com.wec.community.entity.DiscussPost;
import com.wec.community.entity.Page;
import com.wec.community.service.ElasticsearchService;
import com.wec.community.service.LikeService;
import com.wec.community.service.UserService;
import com.wec.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    /***
     * 搜索贴
     * @param keyword 搜索的关键词
     * @param page
     * @param model
     * @return
     */
    ///search?keyword=xxx
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
        elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        if (searchResult != null) {
            //聚合数据
            List<Map<String, Object>> discussPost = new ArrayList<>();
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                //贴子
                map.put("post", post);
                //作者
                map.put("user", userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPost.add(map);
            }
            model.addAttribute("discussPost", discussPost);
            model.addAttribute("keyword", keyword);

            //分页信息
            page.setPath("/search?keyword=" + keyword);
            page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        }
        return "/site/search";
    }
}

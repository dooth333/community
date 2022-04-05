package com.wec.community.service;

import com.wec.community.dao.elasticsearch.DiscussPostRepository;
import com.wec.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchTemplate elasticTemplate;

    public void saveDiscussPost(DiscussPost discussPost){
        discussRepository.save(discussPost);
    }

    public void deleteDiscussPost(int id){
        discussRepository.deleteById(id);
    }

    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit){  //keyword为查询条件

        //构造搜索条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))//搜索条件:   第一个字符串为要搜索的字符串，后面的要搜索的类
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))   //排序条件  SortOrder.DESC为倒叙  type看是否为置顶
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))  //倒叙，把分数高的排到前面
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))  //最后再看时间
                .withPageable(PageRequest.of(current,limit))   //分页，current为第几页，0代表第一页  ，limit为一页多少个
                .withHighlightFields(    //高亮显示的设置
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),  //对title进行高亮显示，preTags关键词前面加<em>,postTags("<em>")后面加<em>
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>") //content高亮显示
                ).build(); //创建


        Page<DiscussPost> page = elasticTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() { //SearchResultMapper为一个接口,匿名实现一下
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                SearchHits hits = searchResponse.getHits(); //返回的为多条数据，而且以json格式
                if (hits.getTotalHits() <= 0){
                    return null;
                }

                List<DiscussPost> list = new ArrayList<>();

                for (SearchHit hit :hits){  //hit为字符串格式
                    DiscussPost post = new DiscussPost();

                    String id = hit.getSourceAsMap().get("id").toString(); //把json中的id读出
                    post.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    // 处理高亮显示
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null){
                        post.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null){
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(post);
                }
                return new AggregatedPageImpl(list,pageable,
                        hits.getTotalHits(),searchResponse.getAggregations(),searchResponse.getScrollId(),hits.getMaxScore());
            }
        });

        return page;
    }
}

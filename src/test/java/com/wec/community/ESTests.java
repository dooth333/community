package com.wec.community;

import com.wec.community.dao.DiscussPostMapper;
import com.wec.community.dao.elasticsearch.DiscussPostRepository;
import com.wec.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ESTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Test
    public void testInsert(){
        //存入单条数据，存三次
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList(){
        //一下存入多条数据
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(138,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(145,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(146,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(11,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(149,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(152,0,100));
    }

    @Test
    public void testUpdate(){
        //修改数据，就是用改变的数据进行覆盖
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，，使劲灌水");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete(){
        //删除一条数据
        discussPostRepository.deleteById(231);
        //删除所有数据
        discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository(){
        //构造搜索条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))//搜索条件:   第一个字符串为要搜索的字符串，后面的要搜索的类
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))   //排序条件  SortOrder.DESC为倒叙  type看是否为置顶
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))  //倒叙，把分数高的排到前面
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))  //最后再看时间
                .withPageable(PageRequest.of(0,10))   //分页，page为第几页，0代表第一页  ，size为一页多少个
                .withHighlightFields(    //高亮显示的设置
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),  //对title进行高亮显示，preTags关键词前面加<em>,postTags("<em>")后面加<em>
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>") //content高亮显示
                ).build(); //创建

//        elasticsearchTemplate.queryForPage(searchQuery,DiscussPost.class,SearchResultMapper); // 底层使用的这个方法
//      底层获取得到了高亮的显示的值，但是没有得到返回值

        Page<DiscussPost> page = discussPostRepository.search(searchQuery);//返回的为一个Page类型对象，但是这个page不是我们创建那个
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        page.forEach(System.out::println);

    }

    @Test
    public void testSearchByTemplate(){
        //构造搜索条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))//搜索条件:   第一个字符串为要搜索的字符串，后面的要搜索的类
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))   //排序条件  SortOrder.DESC为倒叙  type看是否为置顶
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))  //倒叙，把分数高的排到前面
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))  //最后再看时间
                .withPageable(PageRequest.of(0,10))   //分页，page为第几页，0代表第一页  ，size为一页多少个
                .withHighlightFields(    //高亮显示的设置
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),  //对title进行高亮显示，preTags关键词前面加<em>,postTags("<em>")后面加<em>
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>") //content高亮显示
                ).build(); //创建
        Page<DiscussPost> page = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() { //SearchResultMapper为一个接口,匿名实现一下
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

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        page.forEach(System.out::println);
    }
}

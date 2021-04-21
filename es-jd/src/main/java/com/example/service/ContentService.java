package com.example.service;

import com.alibaba.fastjson.JSON;
import com.example.pojo.Content;
import com.example.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @Author: xu
 * @Date: 2021-04-21 10:57
 * @description:
 */
@Service
public class ContentService {


    @Autowired
    private RestHighLevelClient restHighLevelClient;


    public Boolean parseContent(String keyword) throws Exception {
        List<Content> list = new HtmlParseUtil().parseJd(keyword);
        BulkRequest request = new BulkRequest();
        request.timeout("2m");

        for (int i = 0; i < list.size(); i++) {
            request.add(new IndexRequest("jd_goods").source(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        return !bulk.hasFailures();
    }

    public List<Map<String,Object>> searchPage(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo < 1){
            pageNo = 1;
        }
        //条件搜索
        SearchRequest request = new SearchRequest("jd_goods");

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);

        //创建条件构造器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(pageNo)
                .size(pageSize)
                .query(termQueryBuilder).
                        timeout(new TimeValue(60, TimeUnit.SECONDS));

        //执行搜索
        request.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        List<Map<String,Object>> list = new ArrayList<>();
        //解析结果
        for (SearchHit documentFields : search.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

    public List<Map<String,Object>> searchH(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo < 1){
            pageNo = 1;
        }
        //条件搜索
        SearchRequest request = new SearchRequest("jd_goods");

        //精准匹配
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", keyword);

        //创建条件构造器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(pageNo)
                .size(pageSize)
                .query(matchQueryBuilder).
                        timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("title")
                .requireFieldMatch(false)
                .preTags("<span style = 'color:red'>")
                .postTags("</span>");

        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        request.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        List<Map<String,Object>> list = new ArrayList<>();
        //解析结果
        for (SearchHit documentFields : search.getHits().getHits()) {

            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            //原来的结果
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            //解析高亮的字段
            if (title != null){
                Text[] fragments = title.getFragments();
                StringBuilder name = new StringBuilder();
                for (Text text : fragments) {
                    name.append(text);
                }
                sourceAsMap.put("title",name);
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}

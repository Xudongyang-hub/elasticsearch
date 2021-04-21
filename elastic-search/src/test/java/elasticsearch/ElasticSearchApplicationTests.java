package elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pojo.User;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ElasticSearchApplicationTests {

    @Resource
    private RestHighLevelClient client;

    /**
     * 创建索引
     * @throws IOException
     */
    @Test
    void createIndex() throws IOException {
        //创建索引
        CreateIndexRequest request = new CreateIndexRequest("xu_index");
        //客户端执行请求 IndexClient, 请求后获取响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(createIndexResponse);

    }

    /**
     * 获取索引
     */
    @Test
    void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("xu_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(exists);

    }

    /**
     * 删除索引
     */
    @Test
    void deleteIndex() throws IOException{
        DeleteIndexRequest request = new DeleteIndexRequest("xu_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);

        System.out.println(delete.isAcknowledged());

    }
    /**
     * 测试添加文档
     */
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = new User("小明",12);
        //创建请求
        IndexRequest request = new IndexRequest("xu_index");

        //规则 put/xu_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        //将我们数据放入请求
        request.source(JSONObject.toJSONString(user), XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.err.println(indexResponse.toString());
        System.err.println(indexResponse.status());

    }

    //获取文档记录
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("xu_index","1");

        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
        System.err.println(documentFields.getSourceAsString());
        System.err.println(request);
    }


    //更新文档数据
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("xu_index","1");

        User user = new User("老王",41);
        UpdateRequest doc = request.doc(JSON.toJSONString(user),XContentType.JSON);

        UpdateResponse update = client.update(doc, RequestOptions.DEFAULT);
        System.err.println(update.status());
    }

    //删除文档记录
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("xu_index","1");

        DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);

        System.err.println(delete.status());
    }

    //批量插入
    @Test
    void bulkDocument() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");

        List<User> list = new ArrayList<>(5);
        list.add(new User("王五",30));
        list.add(new User("王六",30));
        list.add(new User("王三",30));
        list.add(new User("王二",30));
        list.add(new User("王七",30));

        for (int i = 0; i < list.size(); i++) {
            request.add(new IndexRequest("xu_index")
                    .id(""+(i+1))
                    .source(JSON.toJSONString(list.get(i)),XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(request, RequestOptions.DEFAULT);
        System.err.println(bulk.status());

    }

    //查询
    @Test
    void testSearch() throws IOException {
        SearchRequest request = new SearchRequest("xu_index");
        //创建搜索的构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //利用QueryBuilders 工具 创建搜索方法
        //精确匹配
        TermQueryBuilder age = QueryBuilders.termQuery("age", "30");

        //全部 匹配 查询
//        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

        //注入到构造器里
        searchSourceBuilder.query(age);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //添加请求
        request.source(searchSourceBuilder);
        //发送请求
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        System.err.println(JSON.toJSONString(search.getHits()));

        for (SearchHit hit : search.getHits()) {
            System.err.println(hit.getSourceAsMap());
        }

    }
}

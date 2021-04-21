package com.example.utils;

import com.example.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xu
 * @Date: 2021-04-16 18:18
 * @description: 解析网页
 */
public class HtmlParseUtil {

    public static void main(String[] args) throws Exception {

            new HtmlParseUtil().parseJd("java").forEach(System.out::println);
    }



        public List<Content> parseJd(String keyWords) throws Exception {
        String url = "https://search.jd.com/Search?keyword=" + keyWords+"&enc=utf-8";
        //解析网页 （返回的document就是浏览器的document对象
        Document document = Jsoup.parse(new URL(url),300000);
        // 可以是用js方法
        Element element = document.getElementById("J_goodsList");


        List<Content> goodsList = new ArrayList<>();
        //获取所有的li标签
        Elements elements = element.getElementsByTag("li");
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            goodsList.add(new Content(title,img,price));
        }
        return goodsList;
    }

}

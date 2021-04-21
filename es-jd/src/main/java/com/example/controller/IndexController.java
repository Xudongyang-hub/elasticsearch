package com.example.controller;

import com.example.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: xu
 * @Date: 2021-04-16 18:03
 * @description: index
 */
@RestController
public class IndexController {

    @Autowired
    private ContentService contentService;

    @GetMapping(value = "/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws Exception {

        return contentService.parseContent(keyword);
    }

    @GetMapping(value = "/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable("keyword")String keyword,
                                           @PathVariable("pageNo")int pageNo,
                                           @PathVariable("pageSize")int pageSize) throws IOException {
        return contentService.searchH(keyword, pageNo, pageSize);
    }
}

package com.yiyi.service.agorithm.controller;

import com.yiyi.commom.base.result.R;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/WK")
@CrossOrigin
public class WikiController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getContent")
    public R getContent(@RequestParam("content") String content){

        if (StringUtils.isAllBlank(content)){
            return R.ok();
        }

        String data = restTemplate.getForObject(
                "https://baike.baidu.com/item/" + content, String.class);
        Document document = Jsoup.parse(data);
        System.out.println(data);
        Element element = document.select("meta[name=description]").first();
        String value = element.attr("content");
        System.out.println(value);
        return R.ok().data("item", value);
    }
}

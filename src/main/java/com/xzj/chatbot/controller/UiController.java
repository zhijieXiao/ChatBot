package com.xzj.chatbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author:
 * @Description: html模板静态文件控制
 * @Date: Created in 8:49 2018/3/29
 */
@Controller
public class UiController {

    @RequestMapping("/test")
    @ResponseBody
    String testString() {
        return "test for ui controller connect...";
    }

    @RequestMapping("/chatbot")
    String index() {
        return "index";
    }

}

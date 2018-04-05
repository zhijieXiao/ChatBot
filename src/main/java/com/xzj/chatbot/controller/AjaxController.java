package com.xzj.chatbot.controller;

import com.xzj.chatbot.service.AnsjVerbService;
import com.xzj.chatbot.service.ElasitSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @param
 * @Author:
 * @Description: 前后台交互控制
 * @Date: Created in 10:33 2018/3/29
 */
@Controller
@RequestMapping("/ajax")
public class AjaxController {

    @Autowired
    private AnsjVerbService ansjVerbService;

    @Autowired
    private ElasitSearchService  esService;

    @RequestMapping("/test")
    @ResponseBody
    String getTest(String input) {
        System.out.println(ansjVerbService.testString(input));
        System.out.println(esService.testString(input));
        return "test for ajax controller connect...";
    }

    //HttpServletRequest request
    @RequestMapping("/getHits")
    @ResponseBody
    String getHits(String question) {
        String questionNoTag = question.replaceAll("[\\pP\\p{Punct}]","").replaceAll("br","");
        System.out.println("去除特殊字符后的question:"+questionNoTag);
        return esService.getHits("finance-chatbot3",ansjVerbService.getKeywords(questionNoTag),ansjVerbService.getTern(questionNoTag));
    }

    @RequestMapping("/getAnswer")
    @ResponseBody
    String getAnswer(String id) {
        return esService.getAnswer("finance-chatbot3","agency-reimbursement",id);
    }

    @RequestMapping("/getMoreQuestions")
    @ResponseBody
    String getMoreQuestions(String indistinction,String id){
        return esService.getIndistinction("finance-chatbot3",indistinction,id);
    }

    @RequestMapping("/upLoadLike")
    @ResponseBody
    String upLoadLike(String id,String like){
        esService.upLoadDocument("finance-chatbot3","agency-reimbursement",id,"like",like);
        return "your id:"+id+"  your like:"+like;
    }

}

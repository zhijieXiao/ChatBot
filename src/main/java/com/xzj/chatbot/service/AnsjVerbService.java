package com.xzj.chatbot.service;

import com.xzj.chatbot.ansj.AnsjVerb;
import org.ansj.app.keyword.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @param
 * @Author:
 * @Description:
 * @Date: Created in 11:47 2018/3/29
 */
@Component
public class AnsjVerbService {
    @Autowired
    private AnsjVerb ansj;

    public String testString(String input){
        return ansj.testString(input);
    }

    public Collection<Keyword> getKeywords(String read)
    {
        return ansj.getKeywords(read);
    }

    public Map getTern(String read){
        return ansj.getTern(read);
    }

}

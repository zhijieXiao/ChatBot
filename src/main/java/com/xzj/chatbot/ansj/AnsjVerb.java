package com.xzj.chatbot.ansj;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.SynonymsRecgnition;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author:
 * @Description: ansj分词处理类
 * @Date: Created in 10:31 2018/3/29
 */
@Component
public class AnsjVerb {
    //默认同义词词典
    SynonymsRecgnition synonymsRecgnition ;
    //获取关键字
    private KeyWordComputer kwc;

    private final int KEY_WORD_NUM=5;

    AnsjVerb(){
        this.init();
        System.out.println("AnsjVerb created!!!");
    }

    public void init(){
        this.kwc = new KeyWordComputer(KEY_WORD_NUM);
        this.synonymsRecgnition = new SynonymsRecgnition();
    }

    /**
    * @Description: 获取输入关键字
    * @Param: [read] 输入字符串
    * @Return: java.util.Collection<org.ansj.app.keyword.Keyword>
    */
    public Collection<Keyword> getKeywords(String read){
        Collection<Keyword> result=kwc.computeArticleTfidf( read);
        System.out.println(result.toString());
        return result;
    }

    /**
     * @Description: 根据分词结果获取分词属性
     * @Param: [read] 输入问题
     * @Return: java.util.Map
     */
    public Map<String,VerbProperty> getTern(String read){
        Map<String,VerbProperty> ternMap = new HashMap();
        Result results= NlpAnalysis.parse(read);
        List<Term> termList=results.getTerms();
        for(Term term:results.recognition(synonymsRecgnition)){
            System.out.println(term.getName()+":"+term.getNatureStr()+":"+term.getSynonyms());
            ternMap.put(term.getName(),this.getVerbProperty(term));
        }
        return ternMap;
    }

    /**
    * @Description:
    * @Date: Created in 11:18 2018/4/2
    * @Param: [term] 获取关键字属性
    * @Return: com.xzj.chatbot.ansj.VerbProperty
    */
    public VerbProperty getVerbProperty(Term term){
        VerbProperty property = new VerbProperty();
        property.setNatureStr(term.getNatureStr());
        property.setSynonyms(term.getSynonyms());
        return property;
    }

    public String testString(String input){
        return input;
    }
}

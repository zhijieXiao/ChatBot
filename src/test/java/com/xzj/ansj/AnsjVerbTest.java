package com.xzj.ansj;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.SynonymsRecgnition;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.util.*;

/**
 * @Description: 分词测试类
 * @Date: Created in 9:58 2018/4/2
 */
public class AnsjVerbTest {
    //默认同义词词典
    SynonymsRecgnition synonymsRecgnition ;
    //关键字构造
    KeyWordComputer kwc;
    //预设获取关键字个数
    private  final int KEYWORD_NUM=5;

    public AnsjVerbTest(){
        this.init();
    }

    public void init(){
        this.kwc = new KeyWordComputer(KEYWORD_NUM);
        this.synonymsRecgnition = new SynonymsRecgnition();
    }

    public Collection<Keyword> getKeywords(String read){
        Collection<Keyword> result=kwc.computeArticleTfidf( read);
        System.out.println(result.toString());
        return result;
    }

    /**
     * @Description: 获取输入问题词性
     * @Param: [read] 输入问题
     * @Return: java.util.Map
     */
    public Map getTern(String read){
        Map ternMap = new HashMap();
        Result results= NlpAnalysis.parse(read);//传入forest
        List<Term> termList=results.getTerms();
        for(Term term:termList){
            System.out.println(term.getName()+":"+term.getNatureStr());
            ternMap.put(term.getName(),term.getNatureStr());
        }
        return ternMap;
    }

    /**
    * @Description: 显示查询关键字
    * @Param: [read]
    * @Return: void
    */
    public void printKeyword(String read){
        Collection<Keyword> result = kwc.computeArticleTfidf( read);
        System.out.println("重点词："+result);
    }

    /**
    * @Description: 对输入做分词并显示词性和同义词组
    * @Param: [read]
    * @Return: void
    */
    public void printSynonyms(String read){
        Result results= NlpAnalysis.parse(read);
        List<Term> termList=results.getTerms();
        for(Term term:results.recognition(synonymsRecgnition)){
            System.out.println(term.getName()+":"+term.getNatureStr()+"  Synonyms：" + (term.getSynonyms()));
        }
    }

    public static void main(String[] args) throws Exception {
        AnsjVerbTest test = new AnsjVerbTest();
        Scanner scan = new Scanner(System.in);
        while( scan.hasNext() ){
            //重点词获取
            String read = scan.nextLine();
            test.printKeyword(read);
            test.printSynonyms(read);
        }
    }
}

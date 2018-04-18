package com.xzj.chatbot.common.tool;



import com.xzj.chatbot.es.ElasticSearchClient;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:
 * @Description: excel文件处理类
 * @Date: Created in 16:51 2018/3/28
 */
public class ExcelTool {
    /**
     * @Description: 读取excel文件
     * @Param: [file] 文件
     * @Return: void
     */
    public void readExcel(File file,ElasticSearchClient es ) {
        Map map =new HashMap();
        map.put("frequency",0);
        map.put("like",0);
        map.put("dislike",0);
        String currentItem="";
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                // sheet.getRows()返回该页的总行数
                for (int i = 0; i < sheet.getRows(); i++) {
                    // sheet.getColumns()返回该页的总列数
                    for (int j = 0; j < sheet.getColumns(); j++) {
                        String cellinfo = sheet.getCell(j, i).getContents();
                        System.out.println(cellinfo);
                        if(j == 0){
                            currentItem=cellinfo;
                        }else {
                            getAddMap(map,j,cellinfo);
                        }
                    }
                    es.addDocument(map,currentItem);
                    System.out.println(map.toString());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getAddMap(Map map,int flag,String value){
        switch (flag){
            case 1:
                map.put("groupName", value);
                break;
            case 2:
                map.put("question",value);
                break;
            case 3:
                map.put("indistinction",value);
                break;
            case 4:
                map.put("answer",value);
                break;
        }
    }

    public static void main(String[] args) {
        ExcelTool obj = new ExcelTool();
        ElasticSearchClient es = new ElasticSearchClient();
        es.connection();
        File file = new File("E:/Download/BaiduNetdiskDownload/Y400without win10/Moudle/word/cmbchina/chatbot-library.xls");
        obj.readExcel(file,es);
        es.destroy();
    }
}

package com.venlexi.crawler.core._2021;

/*
       /\   /\             /\.__                      
___  __)/___)/  __ _____  _)/|  |   _______  __ ____  
\  \/  /\__  \ |  |  \  \/ / |  |  /  _ \  \/ // __ \ 
 >    <  / __ \|  |  /\   /  |  |_(  <_> )   /\  ___/ 
/__/\_ \(____  /____/  \_/   |____/\____/ \_/  \___  >
      \/     \/                                    \/
*/

import com.alibaba.fastjson.JSON;
import java.io.PrintWriter;
import java.util.List;

/**
 * @Date 2021/06/04 22:04
 * @Author ling yue
 * @Package com.venlexi.crawler.core._2021
 * @Desc
 */
public class PersistenceService {

    private static final String basePath = "D:\\dev\\projects\\doing\\数据文件\\_2021\\";

    public static void write(List<? extends Object> objects, Boolean spiltLine, String fileName) throws Exception {
        PrintWriter pw = new PrintWriter(basePath + fileName);

        int times = 0;
        for (int i = 0; i < objects.size(); i++) {
            times ++;
            Object object = objects.get(i);
            String jsonString = JSON.toJSONString(object);
            pw.print(jsonString);
            if (i >= objects.size() - 1) {
                break;
            }
            if (spiltLine) {
                pw.println();
            }
            if (times >= 100) {
                pw.flush();
            }
        }
        pw.flush();
        pw.close();
    }
}

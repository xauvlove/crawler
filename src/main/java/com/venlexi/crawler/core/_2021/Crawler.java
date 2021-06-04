package com.venlexi.crawler.core._2021;

/*
       /\   /\             /\.__                      
___  __)/___)/  __ _____  _)/|  |   _______  __ ____  
\  \/  /\__  \ |  |  \  \/ / |  |  /  _ \  \/ // __ \ 
 >    <  / __ \|  |  /\   /  |  |_(  <_> )   /\  ___/ 
/__/\_ \(____  /____/  \_/   |____/\____/ \_/  \___  >
      \/     \/                                    \/
*/

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date 2021/06/04 21:16
 * @Author ling yue
 * @Package com.venlexi.crawler.core._2021
 * @Desc
 */
public class Crawler {

    private static final String 请求学科地址 = "https://yz.chsi.com.cn/zsml/pages/getZy.jsp";

    private static final String 请求专业地址 = "https://yz.chsi.com.cn/zsml/code/zy.do";

    private static final String basePath = "D:\\dev\\projects\\doing\\数据文件\\_2021\\";

    private static final PersistenceService persistenceService = new PersistenceService();

    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();

        crawler.getAndInjectSXSubjects(true);
        crawler.getAndInjectZSSubjects(true);

    }

    /*------------------------------------------------ 学硕 ------------------------------------------------*/

    public List<SXFirstSubject> getAndInjectSXSubjects(Boolean persistence) throws Exception {
        List<SXFirstSubject> SXFirstSubjects = getSXFirstSubjects();
        injectSXSecondarySubjects(SXFirstSubjects);
        injectSXDiscipline(SXFirstSubjects);
        if (persistence) {
            persistenceService.write(SXFirstSubjects, true, "学硕 一级学科+二级学科+三级学科.txt");
        }
        return SXFirstSubjects;
    }
    public ZSFirstSubject getAndInjectZSSubjects(Boolean persistence) throws Exception {
        ZSFirstSubject zxFirstSubjects = getZXFirstSubjects();
        injectZSSecondarySubject(zxFirstSubjects);
        injectZSDiscipline(zxFirstSubjects);
        if (persistence) {
            persistenceService.write(Collections.singletonList(zxFirstSubjects), true, "专硕 一级学科+二级学科+三级学科.txt");
        }
        return zxFirstSubjects;
    }


    /**
     * 学硕一级学科
     * @throws IOException
     */
    public List<SXFirstSubject> getSXFirstSubjects() throws Exception {

        // 所有学硕 一级学科，比较少
        FileInputStream resourceAsStream = new FileInputStream(basePath + "学硕一级学科.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String aLLSXMajorSubjectJson = bufferedReader.readLine();
        List<CommonModel> commonModels = JSONObject.parseArray(aLLSXMajorSubjectJson, CommonModel.class);
        return commonModels.stream().map(model -> {
            SXFirstSubject subject = new SXFirstSubject();
            subject.setSxFirstSubjectName(model.getMc());
            subject.setSxFirstSubjectCode(model.getDm());
            return subject;
        }).collect(Collectors.toList());
    }

    /**
     * 将学硕二级学科注入
     * @param SXFirstSubjects
     * @throws Exception
     */
    public void injectSXSecondarySubjects(List<SXFirstSubject> SXFirstSubjects) throws Exception {
        for (SXFirstSubject SXFirstSubject : SXFirstSubjects) {
            BasicNameValuePair pair = new BasicNameValuePair("mldm", SXFirstSubject.sxFirstSubjectCode);
            String response = post(请求学科地址, Collections.singletonList(pair));
            List<CommonModel> commonModels = JSONObject.parseArray(response, CommonModel.class);
            List<SXSecondarySubject> sxSecondarySubjects = commonModels.stream().map(model -> {
                SXSecondarySubject SXSecondarySubject = new SXSecondarySubject();
                SXSecondarySubject.setSxSecondarySubjectName(model.getMc());
                SXSecondarySubject.setSxSecondarySubjectCode(model.getDm());
                return SXSecondarySubject;
            }).collect(Collectors.toList());
            SXFirstSubject.setSxSecondarySubjects(sxSecondarySubjects);
        }
    }

    public void injectSXDiscipline(List<SXFirstSubject> SXFirstSubjects) throws Exception {
        for (SXFirstSubject SXFirstSubject : SXFirstSubjects) {

            List<SXSecondarySubject> SXSecondarySubjects = SXFirstSubject.getSxSecondarySubjects();
            if (CollectionUtils.isEmpty(SXSecondarySubjects)) {
                continue;
            }
            for (SXSecondarySubject sxSecondarySubject : SXSecondarySubjects) {
                BasicNameValuePair pair = new BasicNameValuePair("q", sxSecondarySubject.getSxSecondarySubjectCode());
                String response = post(请求专业地址, Collections.singletonList(pair));
                if (StringUtils.isBlank(response)) {
                    continue;
                }
                List<String> disciplines = JSONObject.parseArray(response, String.class);
                sxSecondarySubject.setSxDisciplines(disciplines);
            }
        }
    }

    /*------------------------------------------------ 专硕 ------------------------------------------------*/

    public ZSFirstSubject getZXFirstSubjects() throws IOException {
        return new ZSFirstSubject();
    }

    private void injectZSSecondarySubject(ZSFirstSubject zsFirstSubject) throws Exception {
        BasicNameValuePair pair = new BasicNameValuePair("mldm", zsFirstSubject.getZsFirstSubjectCode());
        String response = post(请求学科地址, Collections.singletonList(pair));
        List<CommonModel> commonModels = JSONObject.parseArray(response, CommonModel.class);
        List<ZSSecondarySubject> zsSecondarySubjects = commonModels.stream().map(model -> {
            ZSSecondarySubject zsSecondarySubject = new ZSSecondarySubject();
            zsSecondarySubject.setZsSecondarySubjectName(model.getMc());
            zsSecondarySubject.setZsSecondarySubjectCode(model.getDm());
            return zsSecondarySubject;
        }).collect(Collectors.toList());
        zsFirstSubject.setZsSecondarySubjects(zsSecondarySubjects);
    }

    public void injectZSDiscipline(ZSFirstSubject zsFirstSubject) throws Exception {

        List<ZSSecondarySubject> zsSecondarySubjects = zsFirstSubject.getZsSecondarySubjects();
        if (CollectionUtils.isEmpty(zsSecondarySubjects)) {
            return;
        }
        for (ZSSecondarySubject zsSecondarySubject : zsSecondarySubjects) {
            BasicNameValuePair pair = new BasicNameValuePair("q", zsSecondarySubject.getZsSecondarySubjectCode());
            String response = post(请求专业地址, Collections.singletonList(pair));
            if (StringUtils.isBlank(response)) {
                continue;
            }
            List<String> disciplines = JSONObject.parseArray(response, String.class);
            zsSecondarySubject.setZsDisciplines(disciplines);
        }
    }

    public List<SXSecondarySubject> getZXMajorSubjects() throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("_2021/专硕一级学科.txt");
        assert resourceAsStream != null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String aLLZXMajorSubjectJson = bufferedReader.readLine();
        List<CommonModel> commonModels = JSONObject.parseArray(aLLZXMajorSubjectJson, CommonModel.class);
        return commonModels.stream().map(model -> {
            SXSecondarySubject subject = new SXSecondarySubject();
            subject.setSxSecondarySubjectName(model.getMc());
            subject.setSxSecondarySubjectCode(model.getDm());
            return subject;
        }).collect(Collectors.toList());
    }

    /*------------------------------------------------ city ------------------------------------------------*/

    public List<City> getCity() throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("_2021/省份代号.txt");
        assert resourceAsStream != null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String aLLZXMajorSubjectJson = bufferedReader.readLine();
        List<CommonModel> commonModels = JSONObject.parseArray(aLLZXMajorSubjectJson, CommonModel.class);
        return commonModels.stream().map(model -> {
            City subject = new City();
            subject.setCityName(model.getMc());
            subject.setCityCode(model.getDm());
            return subject;
        }).collect(Collectors.toList());
    }

    /*------------------------------------------------ 爬虫 ------------------------------------------------*/

    public static String post(String postUrl, List<BasicNameValuePair> pairs) throws Exception {
        String content = null;
        //创建 HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(postUrl);
        //创建 List 集合，封装表单参数
        List<NameValuePair> params = new ArrayList<>(pairs);
        //创建 表单 Entity对象，以表单方式提交
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
        //装入 post 请求
        httpPost.setEntity(formEntity);
        /**
         * 配置请求信息
         *
         * 单位：毫秒
         *
         * 设置创建连接的最长时间
         * 设置获取链接的最长时间
         * 设置数据传输的最长时间
         */
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000)
                .setConnectionRequestTimeout(1000).setSocketTimeout(10*1000).build();
        //设置请求信息
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }
        } finally {
            httpClient.close();
        }
        return content;
    }

    /*------------------------------------------------ 模型 ------------------------------------------------*/

    @Data
    private static class SXSecondarySubject {

        /**
         * 次级学科名称
         */
        private String sxSecondarySubjectName;

        /**
         * 次级学科代码
         */
        private String sxSecondarySubjectCode;

        /**
         * 研究方向
         */
        private List<String> sxDisciplines;
    }

    @Data
    private static class SXFirstSubject {

        /**
         * 一级学科名称
         */
        private String sxFirstSubjectName;

        /**
         * 一级学科代码
         */
        private String sxFirstSubjectCode;

        /**
         * 二级学科
         */
        private List<SXSecondarySubject> sxSecondarySubjects;
    }

    @Data
    private static class ZSFirstSubject {

        /**
         * 专硕
         */
        private String zsFirstSubjectName = "专业学位";

        /**
         * 专硕代号：固定 zyxw
         */
        private String zsFirstSubjectCode = "zyxw";

        /**
         * 专业学位二级学科
         */
        private List<ZSSecondarySubject> zsSecondarySubjects;
    }

    @Data
    private static class ZSSecondarySubject {

        /**
         * 次级学科名称
         */
        private String zsSecondarySubjectName;

        /**
         * 次级学科代码
         */
        private String zsSecondarySubjectCode;

        /**
         * 研究方向
         */
        private List<String> zsDisciplines;
    }

    @Data
    private static class City {

        /**
         * 城市/省份名称
         */
        private String cityName;

        /**
         * 城市/省份编码
         */
        private String cityCode;
    }

    @Data
    private static class CommonModel {

        /**
         * 名称
         */
        private String mc;

        /**
         * 代码
         */
        private String dm;
    }
}

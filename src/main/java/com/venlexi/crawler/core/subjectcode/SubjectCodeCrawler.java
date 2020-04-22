package com.venlexi.crawler.core.subjectcode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 1.学术型硕士
 * 2.专业型硕士
 *
 *
 */
public class SubjectCodeCrawler {

    private static final String zhexue = "1001";
    private static final String jingjixue = "1002";
    private static final String faxue = "1003";
    private static final String jiaoyuxue = "1004";
    private static final String wenxue = "1005";
    private static final String lishixue = "1006";
    private static final String lixue = "1007";
    private static final String gongxue = "1008";
    private static final String nongxue = "1009";
    private static final String yixue = "1010";
    private static final String junshixue = "1011";
    private static final String guanlixue = "1012";
    private static final String yishuxue = "1013";

    private static final List<String> XSSubjectCodes = Arrays.asList(zhexue, jingjixue,
            faxue, jiaoyuxue, wenxue, lishixue, lixue, gongxue, nongxue, yixue,
            junshixue, guanlixue, yishuxue);
    private static final List<String> XSSubjectNames = Arrays.asList("哲学", "经济学",
            "法学", "教育学", "文学", "历史学", "理学", "工学", "农学", "医学",
            "军事学", "管理学", "艺术学");


    private static final String _jingjixue = "2002";
    private static final String _faxue = "2003";
    private static final String _jiaoyuxue = "2004";
    private static final String _wenxue = "2005";
    private static final String _lishixue = "2006";
    private static final String _gongxue = "2008";
    private static final String _nongxue = "2009";
    private static final String _yixue = "2010";
    private static final String _junshixue = "2011";
    private static final String _guanlixue = "2012";
    private static final String _yishuxue = "2013";

    private static final List<String> ZSSubjectCodes = Arrays.asList(_jingjixue,
            _faxue, _jiaoyuxue, _wenxue, _lishixue, _gongxue, _nongxue, _yixue,
            _junshixue, _guanlixue, _yishuxue);
    private static final List<String> ZSSubjectNames = Arrays.asList("经济学",
            "法学", "教育学", "文学", "历史学",  "工学", "农学", "医学",
            "军事学", "管理学", "艺术学");


    private static final String POST_HEAD = "https://yz.chsi.com.cn/zyk/specialityCategory.do";
    private static final String POST_METHOD = "subCategoryMl";
    private static final String POST_MTHOD_THIRD = "subCategoryXk";


    public void getAllSubjectNameAndCode() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<FirstSubject> firstSubjectList = new ArrayList<>();
        FileWriter fileWriter = new FileWriter("D:\\apps\\java-develop\\oneonone\\common\\subjectNameAndCodes-zs.txt");
        FileWriter fileWriterForList = new FileWriter("D:\\apps\\java-develop\\oneonone\\common\\subjectNameAndCodes-zs-all.txt");
        int times = 0;
        for(int i = 0; i< ZSSubjectCodes.size(); i++) {
            String content = "";
            HttpPost httpPost = new HttpPost(POST_HEAD);
            List<NameValuePair> params = new ArrayList<>();
            BasicNameValuePair methodPair = new BasicNameValuePair("method", POST_METHOD);
            BasicNameValuePair keyPair = new BasicNameValuePair("key", ZSSubjectCodes.get(i));
            params.add(methodPair);
            params.add(keyPair);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
            //装入 post 请求
            httpPost.setEntity(formEntity);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000)
                    .setConnectionRequestTimeout(1000).setSocketTimeout(10*1000).build();
            //设置请求信息
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse httpResponse = null;

            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                Document document = Jsoup.parse(content);
                FirstSubject firstSubject = new FirstSubject();
                firstSubject.setName(ZSSubjectCodes.get(i));
                firstSubject.setCode(ZSSubjectCodes.get(i));
                firstSubject.setType(1);
                List<SecondSubject> secondSubjectList = new ArrayList<>();
                Elements lis = document.select("li");
                for(int j=0;j<lis.size();j++) {
                    Element element = lis.get(j);
                    Attributes attributes = element.attributes();
                    String secondLevelDiscCode = attributes.get("id");
                    String secondLevelDiscName = element.text();
                    SecondSubject secondSubject = new SecondSubject();
                    secondSubject.setName(secondLevelDiscName);
                    secondSubject.setCode(secondLevelDiscCode);
                    secondSubjectList.add(secondSubject);

                    //查询三级学科
                    httpPost = new HttpPost(POST_HEAD);
                    List<NameValuePair> paramsForThirdSub = new ArrayList<>();
                    BasicNameValuePair methodPairForThird = new BasicNameValuePair("method", POST_MTHOD_THIRD);
                    BasicNameValuePair keyPairForThird = new BasicNameValuePair("key", secondLevelDiscCode);
                    paramsForThirdSub.add(methodPairForThird);
                    paramsForThirdSub.add(keyPairForThird);
                    UrlEncodedFormEntity formEntityForThird = new UrlEncodedFormEntity(paramsForThirdSub, "utf-8");
                    //装入 post 请求
                    httpPost.setEntity(formEntityForThird);
                    RequestConfig requestConfigForThird = RequestConfig.custom().setConnectTimeout(1000)
                            .setConnectionRequestTimeout(1000).setSocketTimeout(10*1000).build();
                    //设置请求信息
                    httpPost.setConfig(requestConfigForThird);
                    httpResponse = httpClient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        Document documentForThird = Jsoup.parse(content);
                        Elements elements = documentForThird.select("table.ch-table tr td");
                        List<ThirdSubject> thirdSubjectList = new ArrayList<>();
                        int nameIndex = 0;
                        int codeIndex = 1;
                        for (int k = 0; k < elements.size(); k=k+4) {
                            String thirdLevelSubName = elements.get(k).text();
                            String thirdLevelSubCode = elements.get(k+1).text();
                            ThirdSubject thirdSubject = new ThirdSubject();
                            thirdSubject.setName(thirdLevelSubName);
                            thirdSubject.setCode(thirdLevelSubCode);
                            thirdSubjectList.add(thirdSubject);
                        }
                        secondSubject.setThirdSubjectList(thirdSubjectList);
                    }

                }
                firstSubject.setSecondSubjectList(secondSubjectList);
                firstSubjectList.add(firstSubject);
                String firstSub = objectMapper.writeValueAsString(firstSubject);
                fileWriter.write(firstSub + "\n");
                System.out.println(ZSSubjectCodes.get(i) + ": " + ZSSubjectCodes.get(i) + " finished");
                times++;
                if(times >= 10) {
                    fileWriter.flush();
                    times = 0;
                }
            }
        }
        String all = objectMapper.writeValueAsString(firstSubjectList);
        fileWriterForList.write(all);
        fileWriter.flush();
        fileWriterForList.flush();
        fileWriter.close();
        fileWriterForList.close();
    }


    public void test() throws Exception {
        FileReader fileReader = new FileReader("D:\\apps\\java-develop\\oneonone\\common\\subjectNameAndCodes-zs.txt");
        BufferedReader br = new BufferedReader(fileReader);
        String content = br.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        FirstSubject firstSubject = objectMapper.readValue(content, FirstSubject.class);
        System.out.println(firstSubject);
    }

    public static void main(String[] args) throws Exception {
        SubjectCodeCrawler crawler = new SubjectCodeCrawler();
        crawler.test();
    }

}

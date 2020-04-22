package com.venlexi.crawler.core.subjectdetailcode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static final String POST_HEAD_FOR_SUBJECT_DETAIL = "https://yz.chsi.com.cn/zsml/querySchAction.do?";
    public List<DetailForRecruitment> detailForRecruitmentList;

    public static void main(String[] args) throws Exception{

        int page = 1;
        Elements compareElements = null;
        List<ThirdSubjectWithRecruitmentNum> thirdSubjectWithRecruitmentNumList = new ArrayList<>();
        for(;page<Integer.MAX_VALUE-5;page++) {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = POST_HEAD_FOR_SUBJECT_DETAIL + "dwmc=" + "南京航空航天大学" + "&";
            url = url + "mldm=" + "zyxw";
            url = url + "&" + "yjxkdm=" + "0854";
            url = url + "&pageno=" + page;
            System.out.println(url);


            HttpGet httpGet  = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            System.out.println("---------------------" + httpResponse.getStatusLine().getStatusCode()+"------------");
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                //TimeUnit.SECONDS.sleep(1);
                String content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                Document document = Jsoup.parse(content);
                Elements elements = document.select("table.ch-table tr td");

                //处理考试方式


                for(int index = 0;index<elements.size();index=index+10) {
                    String testMode = elements.get(index).text();
                    String institute = elements.get(index+1).text();
                    String subject = elements.get(index+2).text();
                    //研究方向
                    String direction = elements.get(index+3).text();
                    //学习方式
                    String learningMode = elements.get(index+4).text();
                    //招生人数
                    Element script = elements.get(index+6).select("script").first();
                    List<DataNode> dataNodes = script.dataNodes();
                    DataNode dataNode = dataNodes.get(0);
                    String data = dataNode.toString();
                    int start = data.indexOf("'");
                    int end = data.lastIndexOf("'");
                    String finalData = data.substring(start+1, end);

                    int numStart = finalData.indexOf("：");
                    int numEnd = finalData.indexOf("(");
                    String finalNumData = finalData.substring(numStart+1, numEnd);
                    String terminalData = "";
                    try {
                        int num = Integer.parseInt(finalNumData);
                        terminalData = num+"";
                    } catch (Exception e) {
                        terminalData = finalData;
                    }
                    //Elements child = script.children();
                    //String data = child.attributes().get("data");
                    //考试范围
                    String testRangeSite =elements.get(index+7).select("a").first().attributes().get("href");
                    ThirdSubjectWithRecruitmentNum thirdSubjectWithRecruitmentNum =
                            new ThirdSubjectWithRecruitmentNum();
                    thirdSubjectWithRecruitmentNum.setTestMode(testMode);
                    thirdSubjectWithRecruitmentNum.setDirection(direction);
                    thirdSubjectWithRecruitmentNum.setInstitute(institute);
                    thirdSubjectWithRecruitmentNum.setLearningMode(learningMode);
                    thirdSubjectWithRecruitmentNum.setNumber(terminalData);
                    thirdSubjectWithRecruitmentNum.setSubject(subject);
                    thirdSubjectWithRecruitmentNum.setTestRangeSite("https://yz.chsi.com.cn" + testRangeSite);
                    thirdSubjectWithRecruitmentNumList.add(thirdSubjectWithRecruitmentNum);
                }

                if(compareElements == null) {
                    compareElements = elements;
                } else {
                    //判断 compareElements 是否等价于 elements
                    //相等，则跳出
                    if(ElementUtil.isElementsSame(elements, compareElements)) {
                        System.out.println(page);
                        page = Integer.MAX_VALUE-1;
                    }
                    compareElements = elements;
                }
            }
        }
        thirdSubjectWithRecruitmentNumList.forEach(System.out::println);
    }
}

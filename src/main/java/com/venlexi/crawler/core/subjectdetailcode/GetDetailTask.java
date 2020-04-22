package com.venlexi.crawler.core.subjectdetailcode;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
public class GetDetailTask implements Runnable{
    private static final String POST_HEAD_FOR_SUBJECT_DETAIL = "https://yz.chsi.com.cn/zsml/querySchAction.do?";
    private List<DetailForRecruitment> detailForRecruitmentList;
    private int start;
    private int end;
    private int index;

    @SneakyThrows
    @Override
    public void run() {

        FileWriter fw = new FileWriter("D:\\apps\\java-develop\\oneonone\\common\\subjectDetail\\fornum\\subjectDetailRecruitment-xs" +index+".txt", true);
        //开始爬虫
        if(index == 5) {
            end = detailForRecruitmentList.size();
        }
        ObjectMapper objectMapper = new ObjectMapper();

        for(int i=start;i<end;i++) {

            CloseableHttpClient httpClient = HttpClients.createDefault();

            DetailForRecruitment detailForRecruitment = detailForRecruitmentList.get(i);
            List<FirstSubjectDetail> firstSubjectDetailList = detailForRecruitment.getFirstSubjectDetailList();


            //一级学科遍历
            for(int first=0;first<firstSubjectDetailList.size();first++) {
                FirstSubjectDetail firstSubjectDetail = firstSubjectDetailList.get(first);
                List<SecondSubjectDetail> secondSubjectDetailList = firstSubjectDetail.getSecondSubjectDetailList();
                //二级学科遍历
                for(int second=0;second<secondSubjectDetailList.size();second++) {
                    SecondSubjectDetail secondSubjectDetail = secondSubjectDetailList.get(second);

                    //用于比较
                    Elements compareElements = null;
                    for(int page=1;page<Integer.MAX_VALUE-5;page++) {
                        //拼接url
                        String url = POST_HEAD_FOR_SUBJECT_DETAIL + "dwmc=" + detailForRecruitment.getSchoolName() + "&";
                        url = url + "mldm=" + firstSubjectDetail.getCode();
                        url = url + "&" + "yjxkdm=" + secondSubjectDetail.getDm() + "&pageno=" + page;
                        System.out.println(url);
                        HttpPost httpPost = new HttpPost(url);
                        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
                        System.out.println("---------------------" + httpResponse.getStatusLine().getStatusCode()+"------------");
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            //TimeUnit.SECONDS.sleep(1);
                            String content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                            Document document = Jsoup.parse(content);

                            //拿到所有的 td
                            Elements elements = document.select("table.ch-table tr td");
                            //处理考试方式
                            List<ThirdSubjectWithRecruitmentNum> thirdSubjectWithRecruitmentNumList = new ArrayList<>();
                            ThirdSubjectWithRecruitmentNum thirdSubjectWithRecruitmentNum = null;
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
                                thirdSubjectWithRecruitmentNum =
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
                                //判断 element 是否等价即可
                                for(int index = 0;index<compareElements.size();index=index+10) {
                                    String testMode = compareElements.get(index).text();
                                    String institute = compareElements.get(index+1).text();
                                    String subject = compareElements.get(index+2).text();
                                    //研究方向
                                    String direction = compareElements.get(index+3).text();
                                    //学习方式
                                    String learningMode = compareElements.get(index+4).text();
                                    //招生人数
                                    Element script = compareElements.get(index+6).select("script").first();
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
                                    String testRangeSite =compareElements.get(index+7).select("a").first().attributes().get("href");
                                    ThirdSubjectWithRecruitmentNum temThirdSubjectWithRecruitmentNum =
                                            new ThirdSubjectWithRecruitmentNum();
                                    temThirdSubjectWithRecruitmentNum.setTestMode(testMode);
                                    temThirdSubjectWithRecruitmentNum.setDirection(direction);
                                    temThirdSubjectWithRecruitmentNum.setInstitute(institute);
                                    temThirdSubjectWithRecruitmentNum.setLearningMode(learningMode);
                                    temThirdSubjectWithRecruitmentNum.setNumber(terminalData);
                                    temThirdSubjectWithRecruitmentNum.setSubject(subject);
                                    temThirdSubjectWithRecruitmentNum.setTestRangeSite("https://yz.chsi.com.cn" + testRangeSite);
                                    compareElements = elements;
                                    if(temThirdSubjectWithRecruitmentNum.isSame(thirdSubjectWithRecruitmentNum)) {
                                        page = Integer.MAX_VALUE - 1;
                                    }
                                }
                            }
                            secondSubjectDetail.setThirdSubWithRecruNumsList(thirdSubjectWithRecruitmentNumList);
                        }
                    }


                }
                firstSubjectDetail.setSecondSubjectDetailList(secondSubjectDetailList);
                //firstSubjectDetailList.add(firstSubjectDetail);

            }
            detailForRecruitment.setFirstSubjectDetailList(firstSubjectDetailList);
            String content = objectMapper.writeValueAsString(detailForRecruitment);
            fw.write(content +"\n");
            fw.flush();
            System.out.println("-----------------------------------" + detailForRecruitment.getSchoolName() + " " +detailForRecruitment.getSchoolId() + " finish------------------------------------");
        }
        fw.flush();
        fw.close();
    }
}

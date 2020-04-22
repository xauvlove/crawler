package com.venlexi.crawler.core.subjectdetailcode;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
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

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@Data
public class GetDetailTask3 implements Runnable{
    private static final String POST_HEAD_FOR_SUBJECT_DETAIL = "https://yz.chsi.com.cn/zsml/querySchAction.do?";
    private List<DetailForRecruitment> detailForRecruitmentList;
    private int start;
    private int end;
    private int index;
    private int masterType;

    @SneakyThrows
    @Override
    public void run() {
        String path = "D:\\apps\\java-develop\\oneonone\\common\\subjectDetail\\fornum\\subjectDetailRecruitment-"+(masterType==1?"xs":"zs") +index+".txt";
        FileWriter fw = new FileWriter(path, true);
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

                    Elements compareElements = null;
                    Elements firstElements = null;
                    List<ThirdSubjectWithRecruitmentNum> thirdSubjectWithRecruitmentNumList = new ArrayList<>();
                    secondSubjectDetail.setThirdSubWithRecruNumsList(thirdSubjectWithRecruitmentNumList);
                    //三级学科搜索
                    for(int page=1;page<=Integer.MAX_VALUE-10;page++) {

                        //拼接url
                        String url = POST_HEAD_FOR_SUBJECT_DETAIL + "dwmc=" + detailForRecruitment.getSchoolName() + "&";
                        url = url + "mldm=" + firstSubjectDetail.getCode();
                        url = url + "&" + "yjxkdm=" + secondSubjectDetail.getDm();
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

                            if(compareElements == null) {
                                compareElements = elements;
                                firstElements = elements;
                            } else {
                                //判断 compareElements 是否等价于 elements
                                //相等，则跳到第二级循环处
                                if(ElementUtil.isElementsSame(elements, firstElements) || ElementUtil.isElementsSame(elements, compareElements)) {
                                    page = Integer.MAX_VALUE-1;
                                    break;
                                } else {
                                    compareElements = elements;
                                }
                            }

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
                        }
                    }
                }
                //firstSubjectDetail.setSecondSubjectDetailList(secondSubjectDetailList);
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

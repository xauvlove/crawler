package com.venlexi.crawler.core.subjectdetailcode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venlexi.crawler.core.subjectcode.FirstSubject;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SubjectDetailCode1 {

    private static final String POST_HEAD = "https://yz.chsi.com.cn/zsml/pages/getZy.jsp";

    private static final String zhuanyexuewei = "zyxw";

    private static final List<FirstSubjectDetail> FIRST_SUBJECT_DETAILS = new ArrayList<>();

    static {
        FirstSubjectDetail zsSubjectDetail = new FirstSubjectDetail();
        zsSubjectDetail.setName(zhuanyexuewei);
        zsSubjectDetail.setCode(zhuanyexuewei);
        FIRST_SUBJECT_DETAILS.add(zsSubjectDetail);

        final FirstSubjectDetail zhexue = new FirstSubjectDetail();
        zhexue.setName("哲学");
        zhexue.setCode("01");
        FIRST_SUBJECT_DETAILS.add(zhexue);

        final FirstSubjectDetail jingjixue = new FirstSubjectDetail();
        jingjixue.setName("经济学");
        jingjixue.setCode("02");
        FIRST_SUBJECT_DETAILS.add(jingjixue);

        final FirstSubjectDetail faxue = new FirstSubjectDetail();
        faxue.setName("法学");
        faxue.setCode("03");
        FIRST_SUBJECT_DETAILS.add(faxue);

        final FirstSubjectDetail jiaoyuxue = new FirstSubjectDetail();
        jiaoyuxue.setName("教育学");
        jiaoyuxue.setCode("04");
        FIRST_SUBJECT_DETAILS.add(jiaoyuxue);

        final FirstSubjectDetail wenxue = new FirstSubjectDetail();
        wenxue.setName("文学");
        wenxue.setCode("05");
        FIRST_SUBJECT_DETAILS.add(wenxue);

        final FirstSubjectDetail lishixue = new FirstSubjectDetail();
        lishixue.setName("历史学");
        lishixue.setCode("06");
        FIRST_SUBJECT_DETAILS.add(lishixue);

        final FirstSubjectDetail lixue = new FirstSubjectDetail();
        lixue.setName("理学");
        lixue.setCode("07");
        FIRST_SUBJECT_DETAILS.add(lixue);

        final FirstSubjectDetail gongxue = new FirstSubjectDetail();
        gongxue.setName("工学");
        gongxue.setCode("08");
        FIRST_SUBJECT_DETAILS.add(gongxue);

        final FirstSubjectDetail nongxue = new FirstSubjectDetail();
        nongxue.setName("农学");
        nongxue.setCode("09");
        FIRST_SUBJECT_DETAILS.add(nongxue);

        final FirstSubjectDetail yixue = new FirstSubjectDetail();
        yixue.setName("医学");
        yixue.setCode("10");
        FIRST_SUBJECT_DETAILS.add(yixue);

        final FirstSubjectDetail junshixue = new FirstSubjectDetail();
        junshixue.setName("军事学");
        junshixue.setCode("11");
        FIRST_SUBJECT_DETAILS.add(junshixue);

        final FirstSubjectDetail guanlixue = new FirstSubjectDetail();
        guanlixue.setName("管理学");
        guanlixue.setCode("12");
        FIRST_SUBJECT_DETAILS.add(guanlixue);

        final FirstSubjectDetail yishuxue = new FirstSubjectDetail();
        yishuxue.setName("艺术学");
        yishuxue.setCode("13");
        FIRST_SUBJECT_DETAILS.add(yishuxue);

    }

    private static final String POST_HEAD_FOR_SUBJECT_DETAIL = "https://yz.chsi.com.cn/zsml/querySchAction.do?";


    public void getAllDetails() throws Exception {

        FileReader frForSchoolName = new FileReader("D:\\apps\\java-develop\\oneonone\\university_name_schoolId.txt");
        BufferedReader brForSchoolName = new BufferedReader(frForSchoolName);

        FileReader frForSubjectCode = new FileReader("D:\\apps\\java-develop\\oneonone\\common\\subjectDetail\\subjectDetailNameAndCodes-xs.txt");
        BufferedReader brForSubjectCode = new BufferedReader(frForSubjectCode);

        FileWriter fw = new FileWriter("D:\\apps\\java-develop\\oneonone\\common\\subjectDetail\\fornum\\subjectDetailRecruitment-xs.txt");

        ObjectMapper objectMapper = new ObjectMapper();
        String subjectDetail = "";

        List<FirstSubjectDetail> allFirstSubList = new ArrayList<>();
        while((subjectDetail = brForSubjectCode.readLine()) != null) {
            FirstSubjectDetail firstSubjectDetail = objectMapper.readValue(subjectDetail, FirstSubjectDetail.class);
            allFirstSubList.add(firstSubjectDetail);
        }
        List<DetailForRecruitment> detailForRecruitmentList1 = new ArrayList<>();
        List<DetailForRecruitment> detailForRecruitmentList2 = new ArrayList<>();
        List<DetailForRecruitment> detailForRecruitmentList3 = new ArrayList<>();
        List<DetailForRecruitment> detailForRecruitmentList4 = new ArrayList<>();
        List<DetailForRecruitment> detailForRecruitmentList5 = new ArrayList<>();
        List<DetailForRecruitment> detailForRecruitmentList6 = new ArrayList<>();
        //循环所有学校
        String schoolInfo = "";
        while((schoolInfo = brForSchoolName.readLine()) != null) {
            String[] split = schoolInfo.split(" ");
            String schoolName = split[0];
            String schoolId = split[1];
            DetailForRecruitment detailForRecruitment = new DetailForRecruitment();
            detailForRecruitment.setSchoolName(schoolName);
            detailForRecruitment.setSchoolId(schoolId);
            detailForRecruitment.setFirstSubjectDetailList(allFirstSubList);

            String content = objectMapper.writeValueAsString(detailForRecruitment);

            DetailForRecruitment detailForRecruitment1 = objectMapper.readValue(content, DetailForRecruitment.class);
            DetailForRecruitment detailForRecruitment2 = objectMapper.readValue(content, DetailForRecruitment.class);
            DetailForRecruitment detailForRecruitment3 = objectMapper.readValue(content, DetailForRecruitment.class);
            DetailForRecruitment detailForRecruitment4 = objectMapper.readValue(content, DetailForRecruitment.class);
            DetailForRecruitment detailForRecruitment5 = objectMapper.readValue(content, DetailForRecruitment.class);
            DetailForRecruitment detailForRecruitment6 = objectMapper.readValue(content, DetailForRecruitment.class);


            detailForRecruitmentList1.add(detailForRecruitment1);
            detailForRecruitmentList2.add(detailForRecruitment2);
            detailForRecruitmentList3.add(detailForRecruitment3);
            detailForRecruitmentList4.add(detailForRecruitment4);
            detailForRecruitmentList5.add(detailForRecruitment5);
            detailForRecruitmentList6.add(detailForRecruitment6);
        }

        int threadNumb = 6;
        int interval = detailForRecruitmentList1.size() / threadNumb;
        int rank = 0;

        GetDetailTask t1 = new GetDetailTask();
        t1.setDetailForRecruitmentList(detailForRecruitmentList1);
        t1.setStart(rank);
        rank = rank + interval;
        t1.setEnd(rank);
        t1.setIndex(0);

        GetDetailTask t2 = new GetDetailTask();
        t2.setDetailForRecruitmentList(detailForRecruitmentList2);
        t2.setStart(rank);
        rank = rank + interval;
        t2.setEnd(rank);
        t2.setIndex(1);

        GetDetailTask t3 = new GetDetailTask();
        t3.setDetailForRecruitmentList(detailForRecruitmentList3);
        t3.setStart(rank);
        rank= rank+interval;
        t3.setEnd(rank);
        t3.setIndex(2);

        GetDetailTask t4 = new GetDetailTask();
        t4.setDetailForRecruitmentList(detailForRecruitmentList4);
        t4.setStart(rank);
        rank = rank + interval;
        t4.setEnd(rank);
        t4.setIndex(3);

        GetDetailTask t5 = new GetDetailTask();
        t5.setDetailForRecruitmentList(detailForRecruitmentList5);
        t5.setStart(rank);
        rank = rank + interval;
        t5.setEnd(rank);
        t5.setIndex(4);

        GetDetailTask t6 = new GetDetailTask();
        t6.setDetailForRecruitmentList(detailForRecruitmentList6);
        t6.setStart(rank);
        rank = rank + interval;
        t6.setEnd(rank);
        t6.setIndex(5);

        Thread thread1 = new Thread(t1);
        Thread thread2 = new Thread(t2);
        Thread thread3 = new Thread(t3);
        Thread thread4 = new Thread(t4);
        Thread thread5 = new Thread(t5);
        Thread thread6 = new Thread(t6);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();

        //开始爬虫
        /*for(int i=0;i<detailForRecruitmentList.size();i++) {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            DetailForRecruitment detailForRecruitment = detailForRecruitmentList.get(i);
            List<FirstSubjectDetail> firstSubjectDetailList = detailForRecruitment.getFirstSubjectDetailList();


            //一级学科遍历
            for(int first=0;first<firstSubjectDetailList.size();first++) {
                FirstSubjectDetail firstSubjectDetail = firstSubjectDetailList.get(first);
                //拼接一级学科



                List<SecondSubjectDetail> secondSubjectDetailList = firstSubjectDetail.getSecondSubjectDetailList();
                //二级学科遍历
                for(int second=0;second<secondSubjectDetailList.size();second++) {
                    SecondSubjectDetail secondSubjectDetail = secondSubjectDetailList.get(second);
                    //拼接二级学科
                    String url = POST_HEAD_FOR_SUBJECT_DETAIL + "dwmc=" + detailForRecruitment.getSchoolName() + "&";
                    url = url + "mldm=" + firstSubjectDetail.getCode();
                    url = url + "&" + "yjxkdm=" + secondSubjectDetail.getDm();
                    System.out.println(url);
                    HttpGet httpGet = new HttpGet(url);
                    CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                    System.out.println("---------------------" + httpResponse.getStatusLine().getStatusCode()+"------------");
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        TimeUnit.SECONDS.sleep(1);
                        String content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        Document document = Jsoup.parse(content);
                        Elements elements = document.select("table.ch-table tr td");
                        //处理考试方式
                        List<ThirdSubjectWithRecruitmentNum> thirdSubjectWithRecruitmentNumList = new ArrayList<>();

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
                        secondSubjectDetail.setThirdSubWithRecruNumsList(thirdSubjectWithRecruitmentNumList);
                    }
                }
                firstSubjectDetail.setSecondSubjectDetailList(secondSubjectDetailList);
                firstSubjectDetailList.add(firstSubjectDetail);

            }
            detailForRecruitment.setFirstSubjectDetailList(firstSubjectDetailList);
            String content = objectMapper.writeValueAsString(detailForRecruitment);
            fw.write(content +"\n");
            fw.flush();
            System.out.println("-----------------------------------" + detailForRecruitment.getSchoolName() + " " +detailForRecruitment.getSchoolId() + " finish------------------------------------");
        }
        fw.flush();
        fw.close();*/
    }

    public void getUniversityCode(int index) throws Exception {
        URL url = null;
        if(index == 1) {
            url = new URL("https://news.koolearn.com/20150617/1053055.html");
        } else {
            url = new URL("https://news.koolearn.com/20150617/1053055" + "_" + index + ".html");
        }
        Document document = Jsoup.parse(url, 2000);
        Elements elements = document.select("div.xqy_core_text tbody");
        FileWriter fw = new FileWriter("D:\\apps\\java-develop\\oneonone\\common\\universityUniqueIdAndName"+index+".txt");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Elements b_td = element.select("td");
            String id = "";
            String name = "";
            for (int j = 4; j < b_td.size()-1; j=j+2) {
                id = b_td.get(j).text();
                name = b_td.get(j+1).text();
                fw.write(id + " " + name + "\n");
            }
        }
        fw.flush();
        fw.close();
    }

    public void getSubjectDetail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<FirstSubject> firstSubjectList = new ArrayList<>();
        FileWriter fileWriter = new FileWriter("D:\\apps\\java-develop\\oneonone\\common\\subjectDetail\\subjectDetailNameAndCodes-zs.txt");
        FileWriter fileWriterForList = new FileWriter("D:\\apps\\java-develop\\oneonone\\common\\subjectDetail\\subjectDetailNameAndCodes-zs-all.txt");
        int times = 0;
        for(int i = 0; i< FIRST_SUBJECT_DETAILS.size(); i++) {
            String content = "";
            HttpPost httpPost = new HttpPost(POST_HEAD);
            List<NameValuePair> params = new ArrayList<>();
            FirstSubjectDetail firstSubjectDetail = FIRST_SUBJECT_DETAILS.get(i);
            BasicNameValuePair mldmPair = new BasicNameValuePair("mldm", firstSubjectDetail.getCode());
            params.add(mldmPair);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
            //装入 post 请求
            httpPost.setEntity(formEntity);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000)
                    .setConnectionRequestTimeout(1000).setSocketTimeout(10 * 1000).build();
            //设置请求信息
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse httpResponse = null;

            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                List<SecondSubjectDetail> secondSubjectDetailList = JSON.parseObject(
                        content, new TypeReference<List<SecondSubjectDetail>>(){});
               // System.out.println(secondSubjectDetailList);
                //Document document = Jsoup.parse(content);
                firstSubjectDetail.setSecondSubjectDetailList(secondSubjectDetailList);
            }
            String detailContent = objectMapper.writeValueAsString(firstSubjectDetail);
            fileWriter.write(detailContent + "\n");
            break;
        }
        fileWriter.flush();
        fileWriter.close();
    }



    public static void main(String[] args) throws Exception {
        SubjectDetailCode1 subjectDetailCode = new SubjectDetailCode1();
            //subjectDetailCode.getUniversityCode(6);
        subjectDetailCode.getAllDetails();
    }
}

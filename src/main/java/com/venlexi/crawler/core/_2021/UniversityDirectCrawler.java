package com.venlexi.crawler.core._2021;

/*
       /\   /\             /\.__                      
___  __)/___)/  __ _____  _)/|  |   _______  __ ____  
\  \/  /\__  \ |  |  \  \/ / |  |  /  _ \  \/ // __ \ 
 >    <  / __ \|  |  /\   /  |  |_(  <_> )   /\  ___/ 
/__/\_ \(____  /____/  \_/   |____/\____/ \_/  \___  >
      \/     \/                                    \/
*/

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Date 2021/06/05 10:58
 * @Author ling yue
 * @Package com.venlexi.crawler.core._2021
 * @Desc
 */
public class UniversityDirectCrawler extends CommonCrawler{

    private static final String 查询具体学科招生情况请求地址 = "https://yz.chsi.com.cn/zsml/querySchAction.do";

    private static final Executor EXECUTOR = Executors.newFixedThreadPool(20);

    private static DataReadFromFileService dataReadFromFileService = new DataReadFromFileService();

    public static void main(String[] args) throws Exception {

        /*List<UniversityRecruitDetail> 重庆大学 = getRecruitmentByUniversity("北京工业大学", sxFirstSubjectFromFile);
        System.out.println(重庆大学);*/
        //File file = new File(basePath + "\\大学研究生招生情况");

       /* List<String> universityNames = new ArrayList<>();
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.length() <= 0) {
                universityNames.add(file1.getName().split(".txt")[0]);
            }
        }
        List<SubjectCrawler.SXFirstSubject> sxFirstSubjectFromFile = dataReadFromFileService.getSXFirstSubjectFromFile();
        for (String universityName : universityNames) {
            List<UniversityRecruitDetail> recruitmentByUniversity = getRecruitmentByUniversity(universityName, sxFirstSubjectFromFile);
            if (recruitmentByUniversity.size() > 0){
                System.out.println("========================================");
                System.exit(-1);
            }
        }*/
    }

    /**
     * 并发获取保持高效招生情况
     * 保存在文件夹-文件中
     * 按照 每个大学是一个 txt 文件进行保存
     * @throws Exception
     */
    public void concurrentGetAndSaveRecruitmentInfo() throws Exception {
        List<SubjectCrawler.SXFirstSubject> sxFirstSubjectFromFile = dataReadFromFileService.getSXFirstSubjectFromFile();
        List<UniversityCrawler.University> universityFromFile = dataReadFromFileService.getUniversityFromFile();
        PersistenceService persistenceService = new PersistenceService();
        CountDownLatch latch = new CountDownLatch(universityFromFile.size());
        for (UniversityCrawler.University university : universityFromFile) {
            EXECUTOR.execute(() -> {
                try {
                    String universityName = university.getUniversityName();
                    List<UniversityRecruitDetail> recruitmentByUniversity = null;
                    try {
                        recruitmentByUniversity = getRecruitmentByUniversity(universityName, sxFirstSubjectFromFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        persistenceService.write(recruitmentByUniversity, true, "\\大学研究生招生情况\\" + universityName + ".txt");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    public static List<UniversityRecruitDetail> getRecruitmentByUniversity(String universityName,
                                                                           List<SubjectCrawler.SXFirstSubject> sxFirstSubjects) throws Exception {
        List<UniversityRecruitDetail> details = new ArrayList<>();
        try {
            for (SubjectCrawler.SXFirstSubject sxFirstSubject : sxFirstSubjects) {
                String sxFirstSubjectCode = sxFirstSubject.getSxFirstSubjectCode();
                List<SubjectCrawler.SXSecondarySubject> sxSecondarySubjects = sxFirstSubject.getSxSecondarySubjects();
                for (SubjectCrawler.SXSecondarySubject sxSecondarySubject : sxSecondarySubjects) {
                    String sxSecondarySubjectCode = sxSecondarySubject.getSxSecondarySubjectCode();
                    String firstPageContent = null;
                    for (int i = 1; i < 10000; i++) {
                        String content = serviceWithParam(查询具体学科招生情况请求地址, universityName, sxFirstSubjectCode, sxSecondarySubjectCode, (i+""));
                        if (Objects.isNull(firstPageContent)) {
                            firstPageContent = content;
                        } else {
                            if (firstPageContent.equals(content)) {
                                break;
                            }
                        }
                        List<UniversityRecruitDetail> universityRecruitDetails = parseUniversityRecruitDetail(universityName, sxFirstSubjectCode, sxSecondarySubjectCode, content);
                        details.addAll(universityRecruitDetails);
                    }
                }
            }
        } catch (ConnectTimeoutException e) {
            return getRecruitmentByUniversity(universityName, sxFirstSubjects);
        } catch (HttpHostConnectException e) {
            return getRecruitmentByUniversity(universityName, sxFirstSubjects);
        }
        return details;
    }

    public static List<UniversityRecruitDetail> parseUniversityRecruitDetail(String universityName, String sxFirstSubjectCode,
                                                                             String sxSecondarySubjectCode, String content) {

        List<UniversityRecruitDetail> details = new ArrayList<>();
        Document document = Jsoup.parse(content);

        // tbody 只有一个 下面包含了所有的招生方向
        Element body = document.select("body").get(0);
        Elements tbody = body.select("tbody");
        // 每一个 tr 就是一个招生方向
        Elements elements = tbody.select("tr");

        for (Element element : elements) {
            UniversityRecruitDetail detail = new UniversityRecruitDetail();
            Elements metaDataInfoElements = element.select("td");
            String testMode = metaDataInfoElements.get(0).text();
            String institute = metaDataInfoElements.get(1).text();
            int codeEnd = institute.indexOf(")");
            String instituteCode = institute.substring(1, codeEnd);
            String instituteName = institute.substring(codeEnd+1);

            String discipline = metaDataInfoElements.get(2).text();
            codeEnd = discipline.indexOf(")");
            String disciplineCode = discipline.substring(1, codeEnd);
            String disciplineName = discipline.substring(codeEnd+1);

            String direction = metaDataInfoElements.get(3).text();
            codeEnd = direction.indexOf(")");
            String directionCode = direction.substring(1, codeEnd);
            String directionName = direction.substring(codeEnd+1);

            String learningMode = metaDataInfoElements.get(4).text();

            String recruitString = metaDataInfoElements.get(6).select("script").toString();
            String spiltStr = "\\(cutString\\('";
            String efficientContent = recruitString.split(spiltStr)[1];
            String recruitContent = efficientContent.split("\\)\\)")[0];
            String[] recruit = recruitContent.split(",");
            String recruitmentNeedTest = recruit[0].substring(0, recruit[0].length()-1);
            String recruitment = recruit[1];

            String testOutline = metaDataInfoElements.get(7).select("a").attr("href");
            Element crossSubjectElement = metaDataInfoElements.get(8);
            String crossSubject = crossSubjectElement.text();

            String remark = "";
            String remarkContent = metaDataInfoElements.get(9).select("script").toString();
            if (StringUtils.isNotBlank(recruitContent)) {
                String[] split = remarkContent.split("\\(cutString\\('");
                if (split.length > 1) {
                    efficientContent = split[1];
                    remark = efficientContent.split("',")[0];
                }
            }

            detail.setUniversityName(universityName);
            detail.setFirstSubjectCode(sxFirstSubjectCode);
            detail.setSecondarySubjectCode(sxSecondarySubjectCode);
            detail.setTestMode(testMode);
            detail.setLearningMode(learningMode);
            detail.setInstituteName(instituteName);
            detail.setInstituteCode(instituteCode);
            detail.setDisciplineCode(disciplineCode);
            detail.setDisciplineName(disciplineName);
            detail.setDirectionCode(directionCode);
            detail.setDirectionName(directionName);
            detail.setSupervisor("");
            detail.setTestOutLine(testOutline);
            detail.setRecruitmentNeedTest(recruitmentNeedTest);
            detail.setRecruitment(recruitment);
            detail.setCrossSubject(crossSubject);
            detail.setRemark(remark);
            details.add(detail);
        }
        return details;
    }

    /**
     *
     * @param url
     * @param dxmc
     * @param mldm
     * @param yjxkdm
     * @return
     * @throws Exception
     */
    private static String serviceWithParam(String url, String dxmc, String mldm, String yjxkdm, String pageno) throws Exception {
        String content = null;
        //创建 HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //http://yun.itheima.com/search?key=Java
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.setParameter("dwmc", dxmc);
        uriBuilder.setParameter("mldm", mldm);
        uriBuilder.setParameter("yjxkdm", yjxkdm);
        uriBuilder.setParameter("pageno", pageno);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }
        } finally {
            httpClient.close();
        }
        return content;
    }

    @Data
    public static class UniversityRecruitDetail {

        /**
         * 大学名称
         */
        private String universityName;

        /**
         * 一级学科代码
         */
        private String firstSubjectCode;

        /**
         * 二级学科代码
         */
        private String secondarySubjectCode;

        /**
         * 考试方式：
         *      统考
         */
        private String testMode;

        /**
         * 学习方式 ：全日制 非全日制
         */
        private String learningMode;

        /**
         * 研究所名称
         */
        private String instituteName;

        /**
         * 研究所编码
         */
        private String instituteCode;

        /**
         * 专业名称
         */
        private String disciplineName;

        /**
         * 专业代码
         */
        private String disciplineCode;

        /**
         * 研究方向
         */
        private String directionName;

        private String directionCode;

        /**
         * 指导老师
         */
        private String supervisor;

        /**
         * 招生人数  不包括推免 exempt
         */
        private String recruitmentNeedTest;

        /**
         * 总招生人数
         */
        private String recruitment;

        /**
         * 考试大纲
         */
        private String testOutLine;

        /**
         * 跨专业
         */
        private String crossSubject;

        /**
         * 备注
         */
        private String remark;
    }
}

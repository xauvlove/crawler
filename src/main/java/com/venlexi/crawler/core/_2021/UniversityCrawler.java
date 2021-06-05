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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2021/06/05 9:39
 * @Author ling yue
 * @Package com.venlexi.crawler.core._2021
 * @Desc
 */
public class UniversityCrawler extends CommonCrawler{

    private static final String 招生院校请求地址 = "https://yz.chsi.com.cn/sch/";

    public static void main(String[] args) throws Exception {
        UniversityCrawler crawler = new UniversityCrawler();
        List<University> universityInfo = crawler.getUniversityInfo();
        PersistenceService persistenceService = new PersistenceService();
        persistenceService.write(universityInfo, true, "具有招生资格的大学.txt");
    }

    private List<University> getUniversityInfo() throws Exception {
        List<University> universities = new ArrayList<>();
        for (int i = 0; i < 43; i++) {
            universities.addAll(extractUniversityInfoByPage(serviceWithParam(招生院校请求地址, "start", String.valueOf(i*20))));
        }
        return universities;
    }

    private List<University> extractUniversityInfoByPage(String pageInfoString) {
        List<University> universities = new ArrayList<>();
        Document document = Jsoup.parse(pageInfoString);
        // 获取页面 body ，每个页面只有一个 body
        Elements elements = document.select("tbody");
        Element bodyElement = elements.get(0);

        // body 中每个 tr 都代表着每个大学
        Elements UniversityElements = bodyElement.select("tr");

        // tr 中每个 td，都代表着 某个大学的所有数据信息
        for (Element universityElement : UniversityElements) {
            Elements universityElementByInfo = universityElement.select("td");
            University university = parseUniversityInfoByUniversityElementInfo(universityElementByInfo);
            universities.add(university);
        }
        return universities;
    }

    /**
     *
     * @param universityElementByInfo 代表着每个大学的所有元数据信息
     *                                并且这些元数据信息按照确定的规则排序
     *                                因此只需按照次序依次取出解析即可
     * @return
     */
    private University parseUniversityInfoByUniversityElementInfo(Elements universityElementByInfo) {

        Element universityNameElement = universityElementByInfo.get(0);
        String universityName = universityNameElement.text();
        Element universityLocationElement = universityElementByInfo.get(1);
        String universityLocation = universityLocationElement.text();
        Element universitySubordinationElement = universityElementByInfo.get(2);
        String universitySubordination = universitySubordinationElement.text();
        Elements urls = universityElementByInfo.select("a");

        // 获取 id
        Element universityIdInfo = urls.get(0);
        Attributes attributes = universityIdInfo.attributes();
        String universityHref = attributes.get("href");
        String universityId = universityHref.replaceAll("/sch/schoolInfo--schId-", "").replaceAll(".dhtml", "");

        //
        Element universityNotationElement = urls.get(1);
        attributes = universityNotationElement.attributes();
        String universityNotation = attributes.get("href");


        Element universityRecruitRegulationElement = urls.get(2);
        attributes = universityRecruitRegulationElement.attributes();
        String universityRecruitRegulation = attributes.get("href");


        Element universityQuestionOnlineElement = urls.get(3);
        attributes = universityQuestionOnlineElement.attributes();
        String universityQuestionOnline = attributes.get("href");

        Element universityAdjustmentElement = urls.get(4);
        attributes = universityAdjustmentElement.attributes();
        String universityAdjustment =attributes.get("href");

        // 下面是判断是否有研究生院和是否自主划线
        boolean hasPostgraduateInstitute = false;
        boolean isPassingScoreBySelf = false;
        Elements select = universityElementByInfo.select(".ch-table-center");
        Element hasPostgraduateInstituteDecideElement = select.get(0);
        if (Objects.nonNull(hasPostgraduateInstituteDecideElement)) {
            Elements i = hasPostgraduateInstituteDecideElement.select("i");
            if (Objects.nonNull(i) && i.size() > 0) {
                hasPostgraduateInstitute = true;
            }
        }
        Element isPassingScoreBySelfDecideElement = select.get(1);
        if (Objects.nonNull(isPassingScoreBySelfDecideElement)) {
            Elements i = isPassingScoreBySelfDecideElement.select("i");
            if (Objects.nonNull(i) && i.size() > 0) {
                isPassingScoreBySelf = true;
            }
        }
        University university = new University();
        university.setUniversityId(universityId);
        university.setUniversityName(universityName);
        university.setUniversityLocation(universityLocation);
        university.setUniversityNotation(universityNotation);
        university.setUniversitySubordination(universitySubordination);
        university.setAdjustmentRegulation(universityAdjustment);
        university.setHasPostgraduateInstitute(hasPostgraduateInstitute);
        university.setPassingScoreBySelf(isPassingScoreBySelf);
        university.setRecruitRegulation(universityRecruitRegulation);
        university.setQuestionOnline(universityQuestionOnline);

        return university;
    }

    private String serviceWithParam(String url, String key, String value) throws Exception {
        String content = null;
        //创建 HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //http://yun.itheima.com/search?key=Java
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.setParameter(key, value);
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
    public static class University {

        /**
         * 大学名
         */
        private String universityName;

        /**
         * 大学 id
         */
        private String universityId;

        /**
         * 大学 id
         */
        private String universityLocation;

        /**
         * 大学 位置 例如：北京 河北
         */
        private String universitySubordination;

        /**
         * 大学 隶属关系，比如隶属于工信部，教育部
         */
        private Boolean hasPostgraduateInstitute;

        /**
         * 大学  是否自主划线
         */
        private Boolean passingScoreBySelf;

        /**
         * 招生公告
         */
        private String universityNotation;

        /**
         * 招生简章
         */
        private String recruitRegulation;

        /**
         * 在线咨询
         */
        private String questionOnline;

        /**
         * 调剂办法
         */
        private String adjustmentRegulation;
    }
}

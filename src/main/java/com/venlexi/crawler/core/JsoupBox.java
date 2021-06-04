package com.venlexi.crawler.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venlexi.crawler.util.ToStringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class JsoupBox {

    private final String prefix = "https://yz.chsi.com.cn/zxdy";

    public List<University> getWithParams(URL url, String condition) throws Exception {
        Document document = Jsoup.parse(url, 10000);
        //获取一个学校的所有信息
        Elements elements = document.select(condition);
        //循环学校
        List<University> universities = new ArrayList<>();
        for (Element element : elements) {
            //获取这个学校的所有信息
            Elements tds = element.select("td");
            //循环处理单个学校信息
            for (int i = 0; i < tds.size(); i++) {
                String name = tds.get(0).text();
                String location = tds.get(1).text();
                String ownTo = tds.get(2).text();
                Elements hasP = tds.get(3).select("i.iconfont");
                boolean hasPostgInstitute = false;
                if(hasP.size()>0) {
                    hasPostgInstitute = true;
                }
                boolean isSelfGivenFractLine = false;
                Elements hasS = tds.get(4).select("i.iconfont");
                if(hasS.size()>0) {
                    isSelfGivenFractLine = true;
                }
                Element entry = tds.get(5).select("a").first();
                Attributes attributes = entry.attributes();
                String questionSite = attributes.get("href");

                entry = tds.get(6).select("a").first();
                attributes = entry.attributes();
                String recruitmentBrochureSite = attributes.get("href");

                entry = tds.get(7).select("a").first();
                attributes = entry.attributes();
                String dispensingRuleSite = attributes.get("href");

                University university = new University();
                university
                        .setName(name)
                        .setLocation(location)
                        .setOwnTo(ownTo)
                        .setDispensingRuleSite(dispensingRuleSite)
                        .setHasPostgInstitute(hasPostgInstitute)
                        .setQuestionSite(questionSite)
                        .setRecruitmentBrochureSite(recruitmentBrochureSite)
                        .setSelfGivenFractLine(isSelfGivenFractLine);
                universities.add(university);
                break;
            }
        }
        return universities;
    }

    public void getAllUniversityAndWriteToFile() throws Exception {
        FileWriter fw = new FileWriter("D:\\dev\\projects\\doing\\server\\files\\universities.txt");
        FileWriter efficientFW = new FileWriter("D:\\dev\\projects\\doing\\server\\files\\universities-ef.txt");
        for(int i=0;i<43;i++) {
            URL url = new URL("https://yz.chsi.com.cn/sch/?start=" + i * 20);
            String condition = "tbody tr";
            List<University> universities = getWithParams(url, condition);
            for (University data : universities) {
                fw.write(data.toString());
                fw.write("\n");
            }
            ToStringUtil<University> toStringUtil = new ToStringUtil<>();
            for (University data : universities) {
                efficientFW.write(toStringUtil.toEfficientString(data));
                efficientFW.write("\n");
            }
        }

        fw.flush();
        fw.close();
        efficientFW.flush();
        efficientFW.close();
    }

    public void getAllZone() throws Exception {
        URL url = new URL("https://yz.chsi.com.cn/sch/");
        Document document = Jsoup.parse(url, 3000);
        Elements elements = document.select("div select option[value]");
        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : elements) {
            Element first = element.select("option[value]").first();
            stringBuilder.append(first.text()).append(" ");
        }
        FileWriter fw = new FileWriter("D:\\dev\\projects\\doing\\server\\files\\alllocations.txt");
        fw.write(stringBuilder.toString());
        fw.flush();
        fw.close();
    }

    public void parseNameAndSchoolId() throws Exception {
        FileReader fr = new FileReader("D:\\dev\\projects\\doing\\server\\files\\universities-ef.txt");
        FileWriter fw = new FileWriter("D:\\dev\\projects\\doing\\server\\files\\university_name_schoolId.txt");
        BufferedReader br = new BufferedReader(fr);
        String schoolUrl = "";
        while((schoolUrl = br.readLine()) != null) {
            String[] split = schoolUrl.split(" ");
            String name = split[0];
            String schoolDetailSite = split[6];
            // /sch/listZszc--schId-367878,categoryId-10460768,mindex-13,start-0.dhtml
            String str = "schId-";
            int indexStart = schoolDetailSite.indexOf(str);
            String str1 = ",categoryId-";
            int indexEnd = schoolDetailSite.indexOf(str1);
            String schoolIdId = schoolDetailSite.substring(indexStart+str.length(), indexEnd);
            fw.write(name + " " + schoolIdId + "\n");
        }
        fw.flush();
        fw.close();
    }

    public void nameAndSite() throws Exception {
        FileReader fr = new FileReader("D:\\apps\\java-develop\\oneonone\\university_name_schoolId.txt");
        FileWriter fw = new FileWriter("D:\\apps\\java-develop\\oneonone\\university_name_site.txt");
        BufferedReader br = new BufferedReader(fr);
        String schoolUrl = "";
        while((schoolUrl = br.readLine()) != null) {
            String[] split = schoolUrl.split(" ");
            String name = split[0];
            String schoolIdId = split[1];
            String baseSitePrefix =  "https://yz.chsi.com.cn/sch/schoolInfo--schId-";
            String baseSiteSuffix = ".dhtml";
            fw.write(name + " " + schoolIdId + " " + baseSitePrefix + schoolIdId + baseSiteSuffix + "\n");
        }
        fw.flush();
        fw.close();
    }

    public void category() throws IOException {
        FileReader fr = new FileReader("D:\\apps\\java-develop\\oneonone\\university_name_site.txt");
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter("D:\\apps\\java-develop\\oneonone\\university_name_categorySite.txt", true);
        FileWriter fwError = new FileWriter("D:\\apps\\java-develop\\oneonone\\university_name_categorySite1-error.txt", true);
        List<String> errorSchoolIds = new ArrayList<>();
        String nameAndSite = "";
        int i = 0;
        boolean start = false;
        while((nameAndSite = br.readLine()) != null) {
            String[] split = nameAndSite.split(" ");
            String name = split[0];
            String schoolId = split[1];
            String schoolSite = split[2];

            /*if(schoolId.equals("367974")) {
                start = true;
                continue;
            }

            if(!start) {
                continue;
            }*/

            URL url = new URL(schoolSite);
            Document document = Jsoup.parse(url, 30000);

            Elements elements = document.select("div ul.yxk-link-list li a");
            Element element = null;
            try {
                element = elements.get(2);

            } catch (Exception e) {
                errorSchoolIds.add(schoolId);
                e.printStackTrace();
                continue;
            }
            Attributes attributes = element.attributes();
            String href = attributes.get("href");
            String s = name + " " + schoolId + " " + schoolSite + " " + href + "\n";
            fw.write(s);
            System.out.println(name + " " + schoolId + " " + href + " finished");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            if(i>=10) {
                fw.flush();
                i = 0;
            }
        }
        fw.flush();
        fw.close();
    }

    public void directionsSite() throws Exception {
        FileReader fr = new FileReader("D:\\apps\\java-develop\\oneonone\\university_name_categorySite.txt");
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter("D:\\apps\\java-develop\\oneonone\\university_name_categorySite-combine.txt", true);

        String categorySite = "";
        int i=0;
        while((categorySite = br.readLine()) != null) {
            String[] split = categorySite.split(" ");
            String name = split[0];
            String schoolId = split[1];
            String site = "https://yz.chsi.com.cn" + split[3];
            fw.write(name + " " + schoolId + " " + site + "\n");
            i++;
            if(i>=10) {
                fw.flush();
                i=0;
            }
        }
        fw.flush();
        fw.close();
    }

    public void parseBigDirection() throws IOException{
        FileReader fr = new FileReader("D:\\apps\\java-develop\\oneonone\\university_name_categorySite-combine.txt");
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter("D:\\apps\\java-develop\\oneonone\\university_name_categoryBigDirection.txt", true);

        String site = "";
        int i=0;
        while((site = br.readLine()) != null) {
            String[] split = site.split(" ");
            String name = split[0];
            String schoolId = split[1];
            site =  split[2];

            URL url = new URL(site);
            Document document = Jsoup.parse(url, 30000);
            //Elements elements = document.select("div.ch-tab.clearfix div.tab-item.js-tab");
            Elements elements = document.select("input[type][name][value]").next().first().select("div.ch-tab");
            fw.write(name + " " + schoolId + " ");

            for(int j=0;j<elements.size();j++) {
                fw.write(elements.get(j).text());
                if(j<elements.size()-1) {
                    fw.write(" ");
                }
            }

            System.out.println(name + " " + schoolId + " " + site + " " + "finished");
            fw.write("\n");
            i++;
            if(i>=10) {
                fw.flush();
                i=0;
            }
        }
        fw.flush();
        fw.close();

    }

    public void parseBigDirectionAndSmall() throws IOException{
        FileReader fr = new FileReader("D:\\apps\\java-develop\\oneonone\\university_name_categorySite-combine.txt");
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter("D:\\apps\\java-develop\\oneonone\\direction\\university_name_categoryBigDirectionAndSmall.txt", true);

        String site = "";
        int i=0;
        while((site = br.readLine()) != null) {
            String[] split = site.split(" ");
            String name = split[0];
            String schoolId = split[1];
            site =  split[2];

            URL url = new URL(site);
            Document document = Jsoup.parse(url, 30000);
            //Elements bigDirectionElements = document.select("div.ch-tab.clearfix div.tab-item.js-tab");
            Elements bigDirectionElements = document.select("input[type][name][value]").next().first().select("div.ch-tab div.tab-item");
            Elements smallDirectionElements = document.select("div.tab-content div.item-content ul.clearfix");


            List<String> bigDirectionList = new ArrayList<>();

            Map<String, List<String>> nameAndCodeMap = new LinkedHashMap<>();
            Map<String, List<String>> nameMap = new LinkedHashMap<>();
            Map<String, List<String>> codeMap = new LinkedHashMap<>();

            for(int j=0;j<bigDirectionElements.size();j++) {
                bigDirectionList.add(bigDirectionElements.get(j).text());

                Elements smallDirectionElementss = smallDirectionElements.get(j).select("ul.clearfix li");
                List<String> smallDirectionNameAndCodeList = new ArrayList<>();
                List<String> smallDirectionNameList = new ArrayList<>();
                List<String> smallDirectionCodeList = new ArrayList<>();
                for(int k=0;k<smallDirectionElementss.size();k++) {
                    String nameAndCode = smallDirectionElementss.get(k).text();
                    int start = nameAndCode.indexOf("[");
                    int end = nameAndCode.indexOf("]");
                    String smallDirectionName = nameAndCode.substring(0, start);
                    String smallDirectionCode = nameAndCode.substring(start+1, end);
                    smallDirectionNameAndCodeList.add(nameAndCode);
                    smallDirectionNameList.add(smallDirectionName);
                    smallDirectionCodeList.add(smallDirectionCode);
                }
                String bigDirectionName = bigDirectionElements.get(j).text();
                nameAndCodeMap.put(bigDirectionName, smallDirectionNameAndCodeList);
                nameMap.put(bigDirectionName, smallDirectionNameList);
                codeMap.put(bigDirectionName, smallDirectionCodeList);
            }
            UniversityDirection universityDirection = new UniversityDirection();
            universityDirection.setName(name);
            universityDirection.setId(schoolId);
            universityDirection.setBigDirection(bigDirectionList);
            universityDirection.setSmallDirectionNameAndCode(nameAndCodeMap);
            universityDirection.setSmallDirectionCodeMap(codeMap);
            universityDirection.setSmallDirectionNameMap(nameMap);

            ObjectMapper objectMapper = new ObjectMapper();
            String valueAsString = objectMapper.writeValueAsString(universityDirection);
            //System.out.println(valueAsString);
            System.out.println(name + " " + schoolId + " " + site + " " + "finished");
            fw.write(valueAsString+"\n");
            i++;
            if(i>=10) {
                fw.flush();
                i=0;
            }
        }
        fw.flush();
        fw.close();
    }




    public static void main(String[] args) throws Exception {
        JsoupBox box = new JsoupBox();
    }
}

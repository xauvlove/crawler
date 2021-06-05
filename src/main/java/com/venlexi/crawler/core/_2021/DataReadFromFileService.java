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
import lombok.Data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2021/06/05 11:05
 * @Author ling yue
 * @Package com.venlexi.crawler.core._2021
 * @Desc
 */
public class DataReadFromFileService extends CommonCrawler {

    public static void main(String[] args) throws Exception {
        getSXFirstSubjectFromFile();
        getUniversityFromFile();
    }

    public static List<UniversityCrawler.University> getUniversityFromFile() throws Exception {
        List<UniversityCrawler.University> universities = new ArrayList<>();
        String path = basePath + "具有招生资格的大学.txt";
        FileInputStream fileInputStream = new FileInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line = "";
        while((line = bufferedReader.readLine()) != null) {
            UniversityCrawler.University university = JSON.parseObject(line, UniversityCrawler.University.class);
            universities.add(university);
        }
        return universities;
    }

    public static List<SubjectCrawler.SXFirstSubject> getSXFirstSubjectFromFile() throws Exception {
        List<SubjectCrawler.SXFirstSubject> sxFirstSubjects = new ArrayList<>();
        String path = basePath + "学硕 一级学科+二级学科+三级学科.txt";
        FileInputStream fileInputStream = new FileInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line = "";
        while((line = bufferedReader.readLine()) != null) {
            SubjectCrawler.SXFirstSubject sxFirstSubject = JSON.parseObject(line, SubjectCrawler.SXFirstSubject.class);
            sxFirstSubjects.add(sxFirstSubject);
        }
        return sxFirstSubjects;
    }
}

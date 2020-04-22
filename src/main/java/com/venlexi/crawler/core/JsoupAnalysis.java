package com.venlexi.crawler.core;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;

public class JsoupAnalysis {

    /**
     * Jsoup 解析 URL
     * @throws Exception
     */
    public void service() throws Exception {
        //解析 url
        Document document = Jsoup.parse(new URL("http://www.itcast.cn"), 1000);
        //使用标签选择器，获取标签的内容
        //可能有多个，我们只需要第一个，因为网页只有一个
        //获取 title 标签内容
        Element titleElement = document.getElementsByTag("title").first();
        String title = titleElement.text();
        System.out.println(title);
    }

    /**
     * Jsoup 解析 html 文件
     * @throws Exception
     */
    public void analysisString() throws Exception {
        //解析 html
        String content = FileUtils.readFileToString(
                new File("D:\\apps\\java-develop\\workspace\\leyou-html\\141.html"), "utf-8");
        Document document = Jsoup.parse(content);
        String title = document.getElementsByTag("title").first().text();
        System.out.println(title);
    }

    /**
     * Jsoup 解析文件
     * @throws Exception
     */
    public void analysisFile() throws Exception {
        Document document = Jsoup.parse( new File("D:\\apps\\java-develop\\workspace\\leyou-html\\141.html"), "utf-8");
        String title = document.getElementsByTag("title").first().text();
        System.out.println(title);
    }

    /**
     * 使用 DOM 解析 document
     * @throws Exception
     */
    public void analysisWithDom() throws Exception {
        Document document = Jsoup.parse( new File("D:\\apps\\java-develop\\workspace\\leyou-html\\141.html"), "utf-8");
        //根据 id 获取元素
        Element itemApp = document.getElementById("itemApp");
        System.out.println(itemApp.text());
        //根据 标签 获取元素
        Element byTag = document.getElementsByTag("li").first();
        System.out.println(byTag.text());
        //根据 class 获取元素
        Element byClass = document.getElementsByClass("part-list unstyled").first();
        System.out.println(byClass.text());
        //根据 属性 获取元素
        Element byAttribute = document.getElementsByAttribute("class").first();
        System.out.println(byAttribute.text());
        //通过 属性 的名字和 值 获取元素
        Element byAttributeAndValue = document.getElementsByAttributeValue("class", "part-list unstyled").first();
        System.out.println(byAttributeAndValue.text());
    }

    public void analysisWithsSelector() throws Exception {
        Document document = Jsoup.parse( new File("D:\\apps\\java-develop\\workspace\\leyou-html\\141.html"), "utf-8");
        //通过 标签 获取元素
        Elements elements = document.select("ul");
        for (Element element : elements) {
            System.out.println(element.text());
        }
        //通过 id 获取元素，语法：#{id}， 以 # 开头，后面跟 id
        Element byId = document.select("#one").first();
        System.out.println(byId.text());
        //通过 class 获取元素，语法：#{.class}，# 开头，后面跟 class
        Element byClass = document.select(".part-list unstyled").first();
        System.out.println(byClass.text());
        //通过 属性 获取元素，语法：[attribute]
        Element byAttribute = document.select("[class]").first();
        System.out.println(byAttribute.text());
        //通过 属性 和 属性值 获取元素
        Element byAttributeAndValue = document.select("[part-list unstyled]").first();
        System.out.println(byAttributeAndValue.text());
    }

    /**
     * 选择器组合使用
     * @throws Exception
     */
    public void combineSelector() throws Exception {
        Document document = Jsoup.parse( new File("D:\\apps\\java-develop\\workspace\\leyou-html\\141.html"), "utf-8");
        // div 标签中含有 'one' 这个 id
        Element first = document.select("div#one").first();
        System.out.println(first.text());
        // li 标签含有 'active' 这个 class
        Element first1 = document.select("li.active").first();
        System.out.println(first1.text());
        // ul 标签含有 'class' 这个属性
        Element first2 = document.select("ul[class]").first();
        System.out.println(first2.text());
        // 任意组合 'div' 标签包含 'index' 这个id，有 'tab-pane-active'这个class，有 'li' 标签
        Element combineSelectElement = document.select("div#index.tab-pane-active li").first();
        System.out.println(combineSelectElement.text());
        // 查找父元素的直接子元素
        Elements first3 = document.select("div#index.tab-pane-active>ul>li");
        System.out.println(first3.text());
        // 查找父元素的所有直接子元素
        Elements first4 = document.select("div#index.tab-pane-active>ul>*");
        System.out.println(first4.text());
    }



    public static void main(String[] args) throws Exception{
        JsoupAnalysis analysis = new JsoupAnalysis();
        //analysis.service();
        //analysis.analysisString();
        //analysis.analysisFile();
        //analysis.analysisWithDom();
        //analysis.analysisWithsSelector();
        analysis.combineSelector();
    }
}

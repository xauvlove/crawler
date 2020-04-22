package com.venlexi.crawler.core.subjectdetailcode;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ElementUtil {

    public static boolean isElementsSame(Elements es1, Elements es2) {
        if(es1.size() != es2.size()) {
            return false;
        }
        for (int i = 0; i < es1.size(); i++) {
            if(!isElementSame(es1.get(i), es2.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isElementSame(Element e1, Element e2) {
        boolean flag = false;
        flag = e1.children().size() == e2.children().size();
        flag = e1.text().equals(e2.text());
        return flag;
    }
}

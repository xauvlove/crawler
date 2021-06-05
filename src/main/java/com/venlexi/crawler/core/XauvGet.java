package com.venlexi.crawler.core;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class XauvGet {
    public static void main(String[] args) {

        String s= "(12)撒大";


        int codeEnd = s.indexOf(")");

        String code = s.substring(1, codeEnd);
        String name = s.substring(codeEnd+1, s.length());

        String[] split = s.split("\\)");
        System.out.println(s);

    }
}

package com.venlexi.crawler.core;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class Crawler {

    private static PoolingHttpClientConnectionManager poolManager;

    public String service() throws Exception {
        String content = null;
        //创建 HttpClient
        //创建 HttpGet 设置访问地址
        //使用 httpGet 发起请求，回去 response
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://www.itcast.com");
            CloseableHttpResponse httpResponse = null;
            httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }
        } finally {
            return content;
        }
    }

    /**
     * 带参的 get 请求
     * @return
     * @throws Exception
     */
    public String serviceWithParam() throws Exception {
        String content = null;
        //创建 HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //http://yun.itheima.com/search?key=Java
        URIBuilder uriBuilder = new URIBuilder("http://yun.itheima.com/search");
        uriBuilder.setParameter("keys", "Java");
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

    /**
     * 无参的 post 请求
     * @return
     * @throws Exception
     */
    public String serviceForPost() throws Exception {
        String content = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://www.itcast.com");
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }
        } finally {
            httpClient.close();
        }
        return content;
    }

    /**
     * 带表单的 post 请求
     * @return
     * @throws Exception
     */
    public String serviceForPostWithForm() throws Exception {
        String content = null;
        //创建 HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://yun.itheima.com/search");
        //创建 List 集合，封装表单参数
        List<NameValuePair> params = new ArrayList<>();
        BasicNameValuePair pair = new BasicNameValuePair("keys", "Java");
        params.add(pair);
        //创建 表单 Entity对象，以表单方式提交
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
        //装入 post 请求
        httpPost.setEntity(formEntity);
        /**
         * 配置请求信息
         *
         * 单位：毫秒
         *
         * 设置创建连接的最长时间
         * 设置获取链接的最长时间
         * 设置数据传输的最长时间
         */
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000)
                .setConnectionRequestTimeout(1000).setSocketTimeout(10*1000).build();
        //设置请求信息
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }
        } finally {
            httpClient.close();
        }
        return content;
    }

    /**
     * 连接池
     *
     * 设置每个主机的最大连接数
     * 为了保证均衡，也就是说，如果我们同时访问 阿里，百度
     * 那么对每个网站的连接数最为 10，以保证每个网站访问均衡
     *
     * @return
     */
    public static HttpClient httpClientPool() {
        //连接池管理器
        if(poolManager == null) {
            poolManager = new PoolingHttpClientConnectionManager();
            //设置最大连接数
            poolManager.setMaxTotal(100);

            poolManager.setDefaultMaxPerRoute(10);
        }
        //每次都从连接池中获取 HttpClient 对象
        return HttpClients.custom().setConnectionManager(poolManager).build();
    }

    public static void closeHttpClientPool() {
        if(poolManager == null) {
            return;
        }
        poolManager.close();
    }

    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        System.out.println(crawler.serviceForPostWithForm());
    }
}

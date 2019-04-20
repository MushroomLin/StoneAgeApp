package com.example.miniresearchdatabase;

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import java.net.*;


public class GetPostUrl {

    public static HttpEntity get(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        HttpEntity result = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            result = response.getEntity();
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public static HttpEntity post(String baseURL) {
//        NameValuePair pair1 = new BasicNameValuePair("username", name);
//        NameValuePair pair2 = new BasicNameValuePair("age", age);

        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
//        pairList.add(pair1);
//        pairList.add(pair2);

        HttpEntity result = null;

        try {
//            HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList);
            // URL使用基本URL即可，其中不需要加参数

            HttpPost httpPost = new HttpPost(baseURL);
            // 将请求体内容加入请求中
//            httpPost.setEntity(requestHttpEntity);
            // 需要客户端对象来发送请求
            HttpClient httpClient = new DefaultHttpClient();
            // 发送请求
            HttpResponse response = httpClient.execute(httpPost);
            // 显示响应
            result = response.getEntity();
        } catch (Exception e) {
            result = null;
        }

        return result;

    }

}
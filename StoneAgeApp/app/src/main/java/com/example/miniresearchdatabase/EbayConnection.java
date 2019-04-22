package com.example.miniresearchdatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.json.*;

import javax.net.ssl.HttpsURLConnection;
// Sample demo for using eBay API Send HTTP Request and get Response
// Reference: https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
// author: Zijian Lin
public class EbayConnection {

    private final String USER_AGENT = "Mozilla/5.0";
    private final String SECURITY_APPNAME = "ZijianLi-DemoApp-PRD-479703086-2aac495c";
    private final String OPERATION_NAME = "findItemsByKeywords";
    private final String SERVICE_VERSION = "1.0.0";
    private final String RESPONSE_DATA_FORMAT = "JSON";
    private final String GLOBAL_ID="EBAY-US";
    private final String SITEID="0";

//        public static void main(String[] args) throws Exception {
//
//                EbayConnection http = new EbayConnection();
//
//                System.out.println("Testing - Send Http GET request");
//                String response = http.sendGet("iPhone8", 3);
//                System.out.println(http.parseResponse(response));
//        }

    // HTTP GET request
    protected String sendGet(String keyword, int numPerPage) throws Exception {
        try {
            String url = "https://svcs.ebay.com/services/search/FindingService/v1?"
                    + "SECURITY-APPNAME="+SECURITY_APPNAME+"&"
                    + "OPERATION-NAME="+OPERATION_NAME+"&"
                    + "SERVICE-VERSION="+SERVICE_VERSION+"&"
                    + "RESPONSE-DATA-FORMAT="+RESPONSE_DATA_FORMAT+"&"
                    //+ "callback=_cb_"+OPERATION_NAME+"&"
                    + "REST-PAYLOAD&"
                    + "keywords="+keyword+"&"
                    + "paginationInput.entriesPerPage="+numPerPage+"&"
                    + "GLOBAL-ID="+GLOBAL_ID+"&"
                    + "siteid="+SITEID;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            Log.w("TAG",response.toString());
            return response.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Double> getPrices(String response) {
        try{
            List<Double> pricesList = new LinkedList<>();
            System.out.println(response);
            JSONObject obj = new JSONObject(response);
            JSONArray arr = obj.getJSONArray("findItemsByKeywordsResponse");
            JSONArray items = arr.getJSONObject(0).
                    getJSONArray("searchResult").getJSONObject(0).getJSONArray("item");
            System.out.println("Number of items: "+items.length());
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONArray sellingStatusArray = item.getJSONArray("sellingStatus");
                JSONObject sellingStatusObject = sellingStatusArray.getJSONObject(0);
                JSONObject currentPriceObject = sellingStatusObject.getJSONArray("currentPrice").getJSONObject(0);
                double price = currentPriceObject.getDouble("__value__");
                pricesList.add(price);
//                result.append("Itemid: "+item.get("itemId").toString()+"\n");
//                result.append("Title: "+item.get("title").toString()+"\n");
//                result.append("URL: "+item.get("viewItemURL").toString()+"\n");
            }
            return pricesList;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
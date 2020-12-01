package com.example.demo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

@SpringBootApplication
public class OpenApiSampleApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(OpenApiSampleApplication.class, args);

        String apiData = callApi();

        String[] data = makeSummaryData(apiData);

        for(int i=0; i <data.length; i++){
            System.out.println(data[i]);
        }
    }

    private static String callApi() throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/AsosDalyInfoService/getWthrDataList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=서비스"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("50", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)*/
        urlBuilder.append("&" + URLEncoder.encode("dataCd","UTF-8") + "=" + URLEncoder.encode("ASOS", "UTF-8")); /*자료 분류 코드*/
        urlBuilder.append("&" + URLEncoder.encode("dateCd","UTF-8") + "=" + URLEncoder.encode("DAY", "UTF-8")); /*날짜 분류 코드*/
        urlBuilder.append("&" + URLEncoder.encode("startDt","UTF-8") + "=" + URLEncoder.encode("20100101", "UTF-8")); /*조회 기간 시작일*/
        urlBuilder.append("&" + URLEncoder.encode("endDt","UTF-8") + "=" + URLEncoder.encode("20100602", "UTF-8")); /*조회 기간 종료일*/
        urlBuilder.append("&" + URLEncoder.encode("stnIds","UTF-8") + "=" + URLEncoder.encode("108", "UTF-8")); /*종관기상관측 지점 번호*/
        URL url = new URL(urlBuilder.toString());
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        return sb.toString();
    }

    private static String[] makeSummaryData(String apiData){

        JSONObject jo = new JSONObject(apiData);
        JSONObject response = jo.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");
        JSONArray item = items.getJSONArray("item");

        String[] locationId = new String[item.length()];
        String[] locationName = new String[item.length()];
        String[] time = new String[item.length()];
        String[] maxTa = new String[item.length()];
        String[] avgTa = new String[item.length()];
        String[] minTa = new String[item.length()];
        String[] avgRhm = new String[item.length()];
        String[] avgWs = new String[item.length()];

        String[] infoSummary = new String[item.length()];

        for(int i=0; i < item.length(); i++) {
            JSONObject info = item.getJSONObject(i);

            locationId[i] = info.getString("stnId"); // 지역
            if(locationId[i].equals("108")){
                locationName[i] = "서울";
            }
            time[i] = info.getString("tm");

            maxTa[i] = info.getString("maxTa"); //최대 기온
            avgTa[i] = info.getString("avgTa");
            minTa[i] = info.getString("minTa");
            avgRhm[i] = info.getString("avgRhm");
            avgWs[i] = info.getString("avgWs"); //평균 풍속
            infoSummary[i] = "지역 : " + locationName[i] +"("+locationId[i]+")" + ", 날짜 : " + time[i] +
                    ", 최대 기온 : " + maxTa[i] + ", 평균 기온 : " + avgTa[i] + ", 최저 기온 : " + minTa[i] +
                    ", 평균 상대 습도 : " + avgRhm[i] + ", 평균 풍속 : " + avgWs[i];
        }
        return infoSummary;
    }

}

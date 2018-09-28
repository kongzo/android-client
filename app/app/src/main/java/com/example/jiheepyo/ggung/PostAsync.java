package com.example.jiheepyo.ggung;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class PostAsync extends AsyncTask<Void, Void, Boolean> {
    final String adjective[] = {"능력있는", "매력있는", "친절한", "게으른", "섹시한", "퉁명스러운", "만사가 귀찮은", "불친절한", "아름다운", "잘생긴", "혁신적인", "코딩하는", "그림그리는", "사색하는"};
    final String noun[] = {"토끼", "도롱뇽", "돼지", "유니콘", "양", "개미", "족제비", "고양이", "코끼리", "금붕어", "잉어", "아나콘다"};
    private double lng;
    private double lat;
    private String content;
    private int layout;
    private String result = "";
    private LoadingDialog loadingDialog;
    private boolean isSetImage = false;
    private String imageEncoded;

    PostAsync(double lng, double lat, String content, int layout, Context context){
        this.lng = lng;
        this.lat = lat;
        this.content = content;
        this.layout = layout;
        loadingDialog = new LoadingDialog(context);
    }

    public void setImage(String base64){
        isSetImage = true;
        imageEncoded = base64;
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL("http://13.125.78.77:8081/api/message"); // 호출할 url
            Map<String,Object> params = new LinkedHashMap<>(); // 파라미터 세팅
            params.put("lng", lng);
            params.put("lat", lat);
            Random random = new Random();
            params.put("nickname", adjective[random.nextInt(adjective.length)] + " " + noun[random.nextInt(noun.length)]);
            params.put("contents", content);
            params.put("layout", layout);
            if(isSetImage){
                params.put("image", "data:image/png:base64," + imageEncoded);
            }

            StringBuilder postData = new StringBuilder();
            for(Map.Entry<String,Object> param : params.entrySet()) {
                if(postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes); // POST 호출

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String inputLine;
            while((inputLine = in.readLine()) != null) { // response 출력
                result += inputLine;
            }
            in.close();

            JSONObject object = new JSONObject(result);
            if(object.getInt("status") != 201) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadingDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        loadingDialog.dismiss();
    }


}

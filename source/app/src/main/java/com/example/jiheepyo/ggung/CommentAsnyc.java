package com.example.jiheepyo.ggung;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class CommentAsnyc extends AsyncTask<String, Void, Boolean> {
    final String adjective[] = {"능력있는", "매력있는", "친절한", "게으른", "섹시한", "퉁명스러운", "만사가 귀찮은", "불친절한", "아름다운", "잘생긴", "혁신적인", "코딩하는", "그림그리는", "사색하는"};
    final String noun[] = {"토끼", "도롱뇽", "돼지", "유니콘", "양", "개미", "족제비", "고양이", "코끼리", "금붕어", "잉어", "아나콘다"};
    private LoadingDialog loadingDialog;
    CommentAsnyc(Context context){
        loadingDialog = new LoadingDialog(context);
    }
    private String nickname = "";
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        //로딩중 다이얼로그제거
        loadingDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //로딩중 다이얼로그생성
        loadingDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        if(strings.length != 2)
            return false;
        try {
            int idx = Integer.parseInt(strings[0]);
            URL url = new URL("http://13.125.78.77:8081/api/comment/" + idx); // 호출할 url
            Map<String,Object> params = new LinkedHashMap<>(); // 파라미터 세팅
            Random random = new Random();
            String adj = adjective[random.nextInt(adjective.length)];
            String no = noun[random.nextInt(noun.length)];
            nickname = adj + " " + no;
            params.put("nickname", nickname);
            params.put("contents", strings[1]);

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

            String result = "";
            String inputLine;
            while((inputLine = in.readLine()) != null) { // response 출력
                result += inputLine;
            }
            in.close();
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt("status") != 201)
                return false;
        }catch (Exception e){
            Log.v("asd", e.toString());
            return false;
        }

        return true;
    }

    public String getNickname() {
        return nickname;
    }
}

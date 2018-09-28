package com.example.jiheepyo.ggung;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class LikeAsync extends AsyncTask<Integer, Void, Boolean> {
    private Context context;
    private LoadingDialog loadingDialog;
    LikeAsync(Context context){
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(Integer... integers) {
        try {
            String url = "http://13.125.78.77:8081/api/like/" + integers[0];
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //전송방식
            con.setRequestMethod("GET");
            //Request Header 정의
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);       //컨텍션타임아웃 5초
            con.setReadTimeout(3000);           //컨텐츠조회 타임아웃 3초

            Charset charset = Charset.forName("UTF-8");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),charset));
            String inputLine;
            String response = "";
            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getInt("status") == 201)
                return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        //로딩중 다이얼로그 제거
        loadingDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //로딩중 다이얼로그 생성
        loadingDialog = new LoadingDialog(context);
        loadingDialog.show();
    }
}

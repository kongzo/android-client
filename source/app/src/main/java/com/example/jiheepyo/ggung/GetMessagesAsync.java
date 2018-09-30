package com.example.jiheepyo.ggung;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetMessagesAsync extends AsyncTask<Void, Void, Boolean> {
    private String result = "";
    private JSONObject jsonObject;
    private LatLng curLocation;
    private LoadingDialog loadingDialog;
    ArrayList<Post> posts;

    GetMessagesAsync(LatLng location, Context context){
        curLocation = location;
        loadingDialog = new LoadingDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadingDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL("http://13.125.78.77:8081/api/messages"); // 호출할 url
            Map<String,Object> params = new LinkedHashMap<>(); // 파라미터 세팅
            params.put("lng", curLocation.longitude);
            params.put("lat", curLocation.latitude);

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

            String inputLine = "";
            while((inputLine = in.readLine()) != null) { // response 출력
                result += inputLine;
            }
            in.close();

            jsonObject = new JSONObject(result);
            Log.v("asd", "status is "+jsonObject.getInt("status"));
            if(jsonObject.getInt("status") != 200){
                return false;
            }

            posts = new ArrayList<>();
            JSONArray array = jsonObject.getJSONArray("result");
            for(int i=0; i<array.length();i++){
                JSONObject object = array.getJSONObject(i);
                JSONObject location = object.getJSONObject("location");
                double lat = (double) location.getJSONArray("coordinates").get(0);
                double lng = (double) location.getJSONArray("coordinates").get(1);
                int likes = object.getInt("likes");
                String nickname = object.getString("nickname");
                String contents = object.getString("contents");
                String rawTime = object.getString("created_at");
                Date writtenTime = getTime(rawTime);
                int layout = object.getInt("layout");
                int idx = object.getInt("idx");
                String image = null;

                posts.add(new Post(idx, lat, lng, likes, nickname, contents, writtenTime, layout));
                if(layout == 4){
                    image = object.getString("image");
                    posts.get(posts.size() - 1).setImageURL(image);
                }

                JSONArray commentArray = object.getJSONArray("comments");
                for(int j=0;j<commentArray.length();j++){
                    JSONObject commentObject = commentArray.getJSONObject(j);
                    String commentRawTime = commentObject.getString("created_at");
                    String id = commentObject.getString("_id");
                    String commentNickname = commentObject.getString("nickname");
                    String commentContents = commentObject.getString("contents");
                    posts.get(posts.size()-1).addComment(new Comment(getTime(commentRawTime), id, commentNickname, commentContents));
                }
            }

        }catch (Exception e){
            Log.v("asd", e.toString());
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        loadingDialog.dismiss();
    }

    private Date getTime(String raw){
        String time = raw.substring(0,10) + "," + raw.substring(11,19);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,hh:mm:ss");
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }
}

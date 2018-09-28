package com.example.jiheepyo.ggung;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

public class WriteActivity extends AppCompatActivity{
    private final static String apiURL = "13.125.78.77:8081/api/message";
    private PostAsync postAsync;
    private int curLayout = 1;
    private View writeView;
    private LoadingDialog loadingDialog;
    private final int PICK_FROM_ALBUM = 1000;
    private static final int CROP_FROM_IMAGE = 2000;
    private String imageEncoded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        init();
    }

    public void init(){
        setClickListener();
        writeView = findViewById(R.id.writeView);
        loadingDialog = new LoadingDialog(this);
    }

    public void setClickListener(){
        ImageButton layoutBtn0 = findViewById(R.id.postLayout0);
        ImageButton layoutBtn1 = findViewById(R.id.postLayout1);
        ImageButton layoutBtn2 = findViewById(R.id.postLayout2);
        ImageButton layoutBtn3 = findViewById(R.id.postLayout3);
        ImageButton layoutBtn4 = findViewById(R.id.postLayout4);
        ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.postLayout0:
                        chageLayout(0);
                        break;
                    case R.id.postLayout1:
                        chageLayout(1);
                        break;
                    case R.id.postLayout2:
                        chageLayout(2);
                        break;
                    case R.id.postLayout3:
                        chageLayout(3);
                        break;
                    case R.id.postLayout4:
                        requestImage();
                        break;
                    default:
                        break;
                }
            }
        };

        layoutBtn0.setOnClickListener(onClickListener);
        layoutBtn1.setOnClickListener(onClickListener);
        layoutBtn2.setOnClickListener(onClickListener);
        layoutBtn3.setOnClickListener(onClickListener);
        layoutBtn4.setOnClickListener(onClickListener);
    }

    public void onCancel(View view) {
        finish();
    }

    public void onPost(View view) throws ExecutionException, InterruptedException {
        Intent intent = getIntent();
        writePost(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lng", 0), getContent(), curLayout);
    }

    public String getContent(){
        EditText editContent = findViewById(R.id.postContent);
        return editContent.getText().toString();
    }

    public void chageLayout(int num){
        EditText editContent = findViewById(R.id.postContent);
        int drawableID = getResources().getIdentifier("m_paper" + num, "drawable", "com.example.jiheepyo.ggung");
        editContent.setBackgroundResource(drawableID);
        curLayout = num;
    }
    public void writePost(double lat, double lng, String content, int layout) throws ExecutionException, InterruptedException {
        postAsync = new PostAsync(lng, lat, content, layout, this);
        if(postAsync.execute().get()){
            Snackbar.make(writeView, "서찰 작성 완료!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            Snackbar.make(writeView, "서찰 작성 실패!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_FROM_ALBUM:
                toCropImage(data);
                break;
            case CROP_FROM_IMAGE:
                if(resultCode != RESULT_OK) {
                    return;
                }
                final Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
                curLayout = 4;
                EditText editContent = findViewById(R.id.postContent);
                editContent.setBackground(new BitmapDrawable(getResources(), bitmap));
                break;
        }
    }

    public void requestImage(){
        if(!checkAppPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})){
            askPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            if(!checkAppPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})){
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    public void changeLayoutByImage(){

        curLayout = 4;
    }

    boolean checkAppPermission(String[] requestPermission){
        boolean[] requestResult=new boolean [requestPermission.length];
        for(int i=0;i<requestResult.length;i++){
            requestResult[i]=(ContextCompat.checkSelfPermission(this, requestPermission[i])
                    == PackageManager.PERMISSION_GRANTED);
            if(!requestResult[i]){return false;}
        }
        return true;
    }

    void askPermission(String[] requestPermission, int REQ_PERMISSION){
        ActivityCompat.requestPermissions(this, requestPermission, REQ_PERMISSION);
    }


    public void toCropImage(Intent data){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data.getData(), "image/*");
        intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
        intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
        intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
        intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_FROM_IMAGE); // CROP_FROM_CAMERA case문 이동
    }
}

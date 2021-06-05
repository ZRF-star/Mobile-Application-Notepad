package com.example.note;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addContent extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG =".addContent";
    private String val;
    private Button savebtn,cancelbtn;
    private EditText etText;
    private ImageView c_img;
    private Uri imageUri;

    //创建数据库对象，准备添加数据
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;

    private File phoneFile,videoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);

        val = getIntent().getStringExtra("flag");
        Log.i(TAG,"这个值是："+val);

        //初始化
        savebtn = findViewById(R.id.save);
        cancelbtn = findViewById(R.id.cancel);
        etText = findViewById(R.id.etText);
        c_img = findViewById(R.id.c_img);

        //v_video = findViewById(R.id.c_video);

        notesDB = new NotesDB(this);
        dbWriter = notesDB.getWritableDatabase();//获取写入数据的权限

        //添加监听事件
        savebtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        initView();
    }

    public void initView(){
        if(val.equals("1")){//添加文字
            c_img.setVisibility(View.GONE);//隐藏
        }
        if(val.equals("2")){//图片
            c_img.setVisibility(View.VISIBLE);//显示
            //创建File对象，用于存储拍照后的图片
            phoneFile = new File(getExternalCacheDir(),getTime()+".jpg");//实例化File
            if(phoneFile.exists()){
                phoneFile.delete();
            }
            try {
                phoneFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                imageUri = FileProvider.getUriForFile(addContent.this, "com.example.Note.fileprovider", phoneFile);
            }else{
                imageUri = Uri.fromFile(phoneFile);
            }
            //跳转到系统相机进行拍照
            Intent i_img = new Intent("android.media.action.IMAGE_CAPTURE");
            i_img.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(i_img,1);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                addDB();
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    //添加数据
    public void addDB(){
        ContentValues cv = new ContentValues();//创建ContentValues对象来封装数据
        cv.put("CONTENT",etText.getText().toString());//调用ContentValues的put方法
        cv.put("TIME",getTime());
        cv.put("PATH",phoneFile+"");//插入到数据库当中
        cv.put("VIDEO",videoFile+"");//视频
        dbWriter.insert("note",null,cv);//insert()方法插入数据
    }

    //获取时间
    public String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date();
        String str = format.format(date);
        return str;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            //通过Bitmap获取路径，展示内容
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            c_img.setImageBitmap(bitmap);//展示图片
        }
    }
}

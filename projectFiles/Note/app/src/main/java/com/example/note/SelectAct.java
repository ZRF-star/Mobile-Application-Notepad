package com.example.note;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SelectAct extends AppCompatActivity implements View.OnClickListener {
    private Button s_delete, s_back;
    private ImageView s_img;
    private TextView s_tv,s_time;
    private NotesDB notesDB;//创建数据库对象
    private SQLiteDatabase dbWriter;//获取写的权限

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        int i = getIntent().getIntExtra("ID",0);
        System.out.println(i);
        s_delete = findViewById(R.id.deletebtn);
        s_back = findViewById(R.id.returnbtn);
        s_img = findViewById(R.id.s_img);
        s_tv = findViewById(R.id.s_tv);
        s_time = findViewById(R.id.s_time);

        notesDB = new NotesDB(this);//实例化
        dbWriter = notesDB.getWritableDatabase();//获取写的权限


        //添加按钮的监听事件
        s_delete.setOnClickListener(this);
        s_back.setOnClickListener(this);

        //进行判断
        if(getIntent().getStringExtra("PATH").equals("null")){
            s_img.setVisibility(View.GONE);
        }else{
            s_img.setVisibility(View.VISIBLE);
        }


        s_tv.setText(getIntent().getStringExtra("CONTENT"));//显示文字内容
        s_time.setText(getIntent().getStringExtra("TIME"));

        Bitmap bitmap = BitmapFactory.decodeFile(getIntent()
                .getStringExtra("PATH"));
        s_img.setImageBitmap(bitmap);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deletebtn:
                dbWriter.delete("note","ID="+
                        getIntent().getIntExtra("ID",0),null);
                notify();
                System.out.println("删除的是："+getIntent().getIntExtra("ID",0));
                //finish();
                break;
            case R.id.returnbtn:
                finish();
                break;
        }
    }


}

package com.example.note;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button textbtn,imgbtn;
    private ListView lv;
    private Intent i;
    private MyAdapter adapter;
    private NotesDB notesDB;
    private SQLiteDatabase dbReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化
        textbtn = findViewById(R.id.text);
        imgbtn = findViewById(R.id.img);
        lv = findViewById(R.id.list);

        //添加监听事件
        textbtn.setOnClickListener(this);
        imgbtn.setOnClickListener(this);


        notesDB = new NotesDB(this);
        dbReader = notesDB.getReadableDatabase();//获取读取权限


        //进入详情页
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private Cursor cursor = dbReader.query("note",null,null,null,null,null,null);

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(cursor.moveToFirst()){
                cursor.moveToPosition(position);
                }
                Intent i = new Intent(MainActivity.this,SelectAct.class);
                i.putExtra("ID",cursor.getInt(cursor.getColumnIndex("ID")));
                i.putExtra("CONTENT",cursor.getString(cursor.getColumnIndex("CONTENT")));
                i.putExtra("TIME",cursor.getString(cursor.getColumnIndex("TIME")));
                i.putExtra("PATH",cursor.getString(cursor.getColumnIndex("PATH")));
                i.putExtra("VIDEO",cursor.getString(cursor.getColumnIndex("VIDEO")));
                startActivity(i);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        i = new Intent(this,addContent.class);//实例化
        switch (v.getId()){
            case R.id.text:
                i.putExtra("flag","1");
                startActivity(i);
                break;

            case R.id.img:
                i.putExtra("flag","2");
                startActivity(i);
                break;

        }

    }

    public void selectDB(){
        Cursor cursor = dbReader.query("note",null,null
        ,null,null,null,null);
        adapter = new MyAdapter(this,cursor);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDB();
    }
}

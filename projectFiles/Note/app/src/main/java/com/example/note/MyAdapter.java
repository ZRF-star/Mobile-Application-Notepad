package com.example.note;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//适配器
public class MyAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private LinearLayout layout;
    public MyAdapter(Context context,Cursor cursor){
        this.context = context;
        this.cursor = cursor;

    }
    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         //加载视图的权限
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (LinearLayout) inflater.inflate(R.layout.cell,null);
        TextView contentTv = layout.findViewById(R.id.list_content);
        TextView timeTv = layout.findViewById(R.id.list_time);
        ImageView imgiv = layout.findViewById(R.id.list_img);

        cursor.moveToPosition(position);
        String content = cursor.getString(cursor.getColumnIndex("CONTENT"));
        String time = cursor.getString(cursor.getColumnIndex("TIME"));
        String url = cursor.getString(cursor.getColumnIndex("PATH"));

        contentTv.setText(content);
        timeTv.setText(time);
        imgiv.setImageBitmap(getImageThumbnail(url,300,300));

        return layout;
    }

    //通过这个方法获取当前的缩略图
    public Bitmap getImageThumbnail(String uri, int width,int height){
        Bitmap bitmap = null;
        //获取缩略图
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(uri,options);

        options.inJustDecodeBounds = false;
        int beWidth = options.outWidth/width;
        int beHeight = options.outHeight/height;
        int be = 1;
        if(beWidth<beHeight){
            be = beWidth;
        }else {
            be = beHeight;
        }
        if(be<=0){
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(uri,options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,width,height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}

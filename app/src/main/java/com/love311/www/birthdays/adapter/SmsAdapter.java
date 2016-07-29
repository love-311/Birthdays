package com.love311.www.birthdays.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.sql.SMSHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/26.
 */
public class SmsAdapter extends BaseAdapter{
    private List<Map<String, Object>> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder viewHolder;
    private SMSHelper dbHelper;
    private SQLiteDatabase db;
    private Toast mToast;
    private boolean[] selected;
    private int flag = 1;
    public SmsAdapter(Context context, List<Map<String, Object>> list) {
        mList = list;
        mInflater =LayoutInflater.from(context);
        mContext = context;
        dbHelper = new SMSHelper(mContext,"SmsContent.db",null,1);
        selected = new boolean[list.size()];
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView ==null){
            convertView = mInflater.inflate(R.layout.msg_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.msg_content);
          //  viewHolder.iv_collection = (ToggleButton) convertView.findViewById(R.id.iv_collection);
            viewHolder.iv_collection = (ImageView) convertView.findViewById(R.id.iv_collection);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_content.setText((String) mList.get(position).get("content"));
            if (mList.get(position).get("status").equals(0)){
                viewHolder.iv_collection.setBackgroundResource(R.drawable.heart);
                selected[position] =false;
                // viewHolder.iv_collection.setChecked(false);
            }else if(mList.get(position).get("status").equals(1)){
                viewHolder.iv_collection.setBackgroundResource(R.drawable.heart_selected);
                selected[position] = true;
                // viewHolder.iv_collection.setChecked(true);
            }
        viewHolder.iv_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected[position]==false){
                    selected[position]=true;
                    v.setBackgroundResource(R.drawable.heart_selected);
                    db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("status",1);
                    db.update("Sms",values,"id = ?",new String[]{mList.get(position).get("id")+""});
                    // Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    showToast("已收藏");
                }else if (selected[position]==true){
                    selected[position]=false;
                    v.setBackgroundResource(R.drawable.heart);
                    db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("status",0);
                    db.update("Sms",values,"id = ?",new String[]{mList.get(position).get("id")+""});
                    // Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    showToast("取消收藏");
                }
            }
        });
//        viewHolder.iv_collection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//   //             buttonView.setBackgroundResource(R.drawable.heart_selected);
//                    db = dbHelper.getWritableDatabase();
//                    ContentValues values = new ContentValues();
//                    values.put("status",1);
//                    db.update("Sms",values,"id = ?",new String[]{mList.get(position).get("id")+""});
//                   // Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
//                    showToast("已收藏");
//                }else {
//         //          buttonView.setBackgroundResource(R.drawable.heart);
//                    db = dbHelper.getWritableDatabase();
//                    ContentValues values = new ContentValues();
//                    values.put("status",0);
//                    db.update("Sms",values,"id = ?",new String[]{mList.get(position).get("id")+""});
//                    //Toast.makeText(mContext,"取消收藏",Toast.LENGTH_SHORT).show();
//                    showToast("取消收藏");
//                }
//            }
//        });
        return convertView;
    }


    //避免toast重复显示
    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
    public class ViewHolder {
       private TextView tv_content;
       // private ToggleButton iv_collection;
       private ImageView iv_collection;
    }
}

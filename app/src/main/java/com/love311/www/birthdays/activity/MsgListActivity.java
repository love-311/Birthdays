package com.love311.www.birthdays.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.adapter.SmsAdapter;
import com.love311.www.birthdays.sql.SMSHelper;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/26.
 */
public class MsgListActivity extends AutoLayoutActivity{

    private int flag;
    private ListView lv_msg;
    private SMSHelper dbHelper;
    private SQLiteDatabase db;
    private List<Map<String,Object>> data;
    private Map<String,Object> map;
    private String content;
    private int status;
    private int id;
    private SmsAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_list);
        lv_msg = (ListView) findViewById(R.id.lv_msg_list);
        Bundle bundle = getIntent().getExtras();
        flag = bundle.getInt("flag");
        dbHelper = new SMSHelper(MsgListActivity.this,"SmsContent.db",null,1);
        db = dbHelper.getWritableDatabase();
        data = new ArrayList<>();
        Log.d("MsgListActivity",flag+"");
        Cursor cursor = db.query("Sms",new String[]{"flag","id","content","status"},"flag = ?",new String[]{flag+""},null,null,null);
       // Cursor cursor = db.query("Sms",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                content = cursor.getString(cursor.getColumnIndex("content"));
                status = cursor.getInt(cursor.getColumnIndex("status"));
                id = cursor.getInt(cursor.getColumnIndex("id"));
                map = new HashMap<>();
                map.put("content",content);
                map.put("status",status);
                map.put("id",id);
                data.add(map);
            }while (cursor.moveToNext());
        }
        cursor.close();
        adapter = new SmsAdapter(MsgListActivity.this,data);
        lv_msg.setAdapter(adapter);
        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MsgListActivity.this,MessageWishActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("content", (String) data.get(position).get("content"));
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                // 进行资源释放操作
                break;
        }
    }
}

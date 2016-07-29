package com.love311.www.birthdays.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.activity.BirthInfoActivity;
import com.love311.www.birthdays.adapter.BirthDayAdapter;
import com.love311.www.birthdays.sql.BirthInfoHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.xhinliang.lunarcalendar.utils.SolarTerm;

/**
 * Created by Administrator on 2016/5/20.
 */
public class BirthdayFragment extends Fragment{

    private View view;
    private ListView lv_birth;
    private BirthInfoHelper dbHelper;
    private SQLiteDatabase db;
    private List<Map<String,Object>> data;
    private Map<String,Object> map;
    private BirthDayAdapter adapter;
    private String name,sex,birth,phone,remarks,lunarBirth;
    private byte[] icon;
    private int lunarYear,lunarMonth,lunarDay,lunarLoop,flag,id,solarMoth,solarDay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.birth_frag,container,false);
        lv_birth = (ListView) view.findViewById(R.id.lv_birth);
        dbHelper = new BirthInfoHelper(getActivity(),"BirthInfo.db",null,1);
        db = dbHelper.getWritableDatabase();
        data = new ArrayList<>();
        Cursor cursor = db.query("Birth",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                 name = cursor.getString(cursor.getColumnIndex("name"));
                 sex = cursor.getString(cursor.getColumnIndex("sex"));
                 birth = cursor.getString(cursor.getColumnIndex("birth"));
                 phone = cursor.getString(cursor.getColumnIndex("phone"));
                 remarks = cursor.getString(cursor.getColumnIndex("remarks"));
                 lunarBirth = cursor.getString(cursor.getColumnIndex("lunar_birth"));
                 icon = cursor.getBlob(cursor.getColumnIndex("icon"));//取出图片
                 lunarYear =  cursor.getInt(cursor.getColumnIndex("lunar_year"));
                 lunarMonth =  cursor.getInt(cursor.getColumnIndex("lunar_month"));
                 lunarDay =  cursor.getInt(cursor.getColumnIndex("lunar_day"));
                 solarMoth =  cursor.getInt(cursor.getColumnIndex("solar_month"));
                 solarDay =  cursor.getInt(cursor.getColumnIndex("solar_day"));
                 lunarLoop =  cursor.getInt(cursor.getColumnIndex("lunar_loop"));
                 flag = cursor.getInt(cursor.getColumnIndex("flag"));
                 id = cursor.getInt(cursor.getColumnIndex("id"));
                map = new HashMap<>();
                map.put("name",name);
                map.put("birth",birth);
                map.put("flag",flag);
                map.put("sex",sex);
                map.put("lunar_birth",lunarBirth);
                map.put("lunar_year",lunarYear);
                map.put("lunar_month",lunarMonth);
                map.put("lunar_day",lunarDay);
                map.put("solar_month",solarMoth);
                map.put("solar_day",solarDay);
                map.put("lunar_loop",lunarLoop);
                map.put("id",id);
                map.put("icon", icon);
                map.put("remarks",remarks);
                map.put("phone",phone);
                data.add(map);
                Log.d("BirthDayFragment",name);
                Log.d("BirthDayFragment",sex);
                Log.d("BirthDayFragment",birth);
                Log.d("BirthDayFragment",phone);
                Log.d("BirthDayFragment",remarks);
                Log.d("BirthDayFragment",flag+"");
                Log.d("BirthDayFragment",lunarBirth);
                Log.d("BirthDayFragment",icon+"");
                Log.d("BirthDayFragment",lunarYear+"");
                Log.d("BirthDayFragment",lunarMonth+"");
                Log.d("BirthDayFragment",lunarDay+"");
                Log.d("BirthDayFragment",lunarLoop+"");
            }while (cursor.moveToNext());
        }
        cursor.close();
        adapter = new BirthDayAdapter(getActivity(),data);
        lv_birth.setAdapter(adapter);
        lv_birth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BirthInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", (String) data.get(position).get("name"));
                bundle.putString("sex",(String)data.get(position).get("sex"));
                bundle.putByteArray("icon", (byte[]) data.get(position).get("icon"));
                bundle.putInt("flag",(int)data.get(position).get("flag"));
                bundle.putString("birth",(String)data.get(position).get("birth"));
                bundle.putString("lunar_birth",(String)data.get(position).get("lunar_birth"));
                bundle.putInt("lunar_month",(int)data.get(position).get("lunar_month"));
                bundle.putInt("lunar_day",(int)data.get(position).get("lunar_day"));
                bundle.putInt("solar_month",(int)data.get(position).get("solar_month"));
                bundle.putInt("solar_day",(int)data.get(position).get("solar_day"));
                bundle.putInt("lunar_loop",(int)data.get(position).get("lunar_loop"));
                bundle.putString("remarks",(String)data.get(position).get("remarks"));
                bundle.putString("phone",(String)data.get(position).get("phone"));
                bundle.putInt("id", (int) data.get(position).get("id"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;

    }
}

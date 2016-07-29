package com.love311.www.birthdays.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.utils.LunarCalendar;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/23.
 */
public class BirthInfoActivity extends AutoLayoutActivity{

    private TextView tv_name;
    private ByteArrayInputStream bais;
    private Bitmap bitmap;
    private ImageView iv_icon;
    private TextView tv_left;
    private int flag;
    private TextView tv_right;
    private int[] lunar = new int[4];
    private TextView tv_remark;
    private LinearLayout ll_phone,ll_modification;
    private ImageView iv_phone,iv_send_message;
    private TextView tv_birthday;
    private LinearLayout ll_rtn;
    private int id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birth_info);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_icon = (ImageView) findViewById(R.id.user_icon);
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_remark = (TextView) findViewById(R.id.tv_remarks);
        iv_phone = (ImageView) findViewById(R.id.iv_phone);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        ll_modification = (LinearLayout) findViewById(R.id.ll_modification);
        iv_send_message = (ImageView) findViewById(R.id.iv_send_message);
        ll_rtn = (LinearLayout) findViewById(R.id.ll_rtn);
        final Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String sex = bundle.getString("sex");
        byte[] icon = bundle.getByteArray("icon");
        flag = bundle.getInt("flag");
        String birth = bundle.getString("birth");
        int lunarMonth = bundle.getInt("lunar_month");
        int lunarDay = bundle.getInt("lunar_day");
        int lunarLoop = bundle.getInt("lunar_loop");
        String remarks = bundle.getString("remarks");
        final String phone = bundle.getString("phone");
        String lunarBirth = bundle.getString("lunar_birth");
        id = bundle.getInt("id");
        ll_rtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //修改好友信息
        ll_modification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BirthInfoActivity.this,ModificationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id",id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //发送短信
        iv_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BirthInfoActivity.this,MessageWishActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id",id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //拨打电话
        iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone);
                intent.setData(data);
                startActivity(intent);
            }
        });
        tv_name.setText(name);
        if (remarks !=null && !remarks.equals("")){
            tv_remark.setText(remarks);
        }else {
            tv_remark.setText("未添加备注信息");
        }
        if (icon != null) {
            bais = new ByteArrayInputStream(icon);
            bitmap = BitmapFactory.decodeStream(bais);
            iv_icon.setImageBitmap(bitmap);
        } else if (sex.equals("他")) {
            iv_icon.setImageResource(R.mipmap.male);
        } else {
            iv_icon.setImageResource(R.mipmap.girl);
        }
        if (sex.equals("他")){
            if (flag == 0){
                tv_left.setText("离他的公历生日");
                tv_right.setText(getSolarBirthday(birth));
                tv_birthday.setText(birth);
            }else {
                tv_left.setText("离他的农历生日");
                tv_birthday.setText(lunarBirth);
                Calendar cal = Calendar.getInstance();
                int yearNow = cal.get(Calendar.YEAR);// 获得当前年份
                if (lunarLoop == 0) {
                    lunar = LunarCalendar.lunarToSolar(yearNow, lunarMonth, lunarDay, false);
                } else {
                    lunar = LunarCalendar.lunarToSolar(yearNow, lunarMonth, lunarMonth, true);
                    //闰月就加一个月
                    lunar[1] = lunar[1] + 1;
                }
                String lunar_date = lunar[0] + "-" + lunar[1] + "-" + lunar[2];
                Log.d("BirthDayAdapter", lunar_date + "++++++++++++");
                tv_right.setText(getSolarBirthday(lunar_date));
            }
        }else {
            if (flag == 0){
                tv_left.setText("离她的公历生日");
                tv_birthday.setText(birth);
                tv_right.setText(getSolarBirthday(birth));
            }else {
                tv_left.setText("离她的农历生日");
                tv_birthday.setText(lunarBirth);
                Calendar cal = Calendar.getInstance();
                int yearNow = cal.get(Calendar.YEAR);// 获得当前年份
                if (lunarLoop == 0) {
                    lunar = LunarCalendar.lunarToSolar(yearNow, lunarMonth, lunarDay, false);
                } else {
                    lunar = LunarCalendar.lunarToSolar(yearNow, lunarMonth, lunarMonth, true);
                    //闰月就加一个月
                    lunar[1] = lunar[1] + 1;
                }
                String lunar_date = lunar[0] + "-" + lunar[1] + "-" + lunar[2];
                Log.d("BirthDayAdapter", lunar_date + "++++++++++++");
                tv_right.setText(getSolarBirthday(lunar_date));
            }
        }
    }

    //距离公历生日还有多久
    public String getSolarBirthday(String birthday) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);// 获得当前年份
        try {
            Log.i("Birthday_time", formatter.parse(birthday) + "");
            cal.setTime(formatter.parse(birthday));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int birthyear = cal.get(Calendar.YEAR);
        Log.i("Birthday_year", birthyear + "");
        while (birthyear < yearNow) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
            birthyear = cal.get(Calendar.YEAR);
            Log.i("Birthday_year++", birthyear + "");
        }
        Date ed = new Date();
        Log.i("Date_ed", ed + "");
        Date sd = cal.getTime();
        Log.i("Date_sd", sd + "");
        long days = 0;
        if ((ed.getTime() - sd.getTime()) / (3600 * 24 * 1000) < 0) {//>
            days = -((ed.getTime() - sd.getTime()) / (3600 * 24 * 1000)) + 1;
            System.out.println("距离你生日还有" + days + "天");
            if (days >= 365) {
                days = days - 365;
            }
            return days + "天";
        } else {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
            sd = cal.getTime();
            days = -((ed.getTime() - sd.getTime()) / (3600 * 24 * 1000)) + 1;
            System.out.println("距离你生日还有" + days + "天");
            if (days >= 365) {
                days = days - 365;
            }
            return days + "天";
        }
    }
}

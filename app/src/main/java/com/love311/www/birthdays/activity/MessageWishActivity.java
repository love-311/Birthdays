package com.love311.www.birthdays.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.sql.BirthInfoHelper;
import com.love311.www.birthdays.utils.ActivityMeg;
import com.love311.www.birthdays.view.ScreenInfo;
import com.love311.www.birthdays.view.WheelMain;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2016/5/24.
 */
public class MessageWishActivity extends AutoLayoutActivity implements EasyPermissions.PermissionCallbacks{

    /** 短信内容 **/
    private String smsInfo, sms_colleat;
    private BirthInfoHelper dbHelper;
    private SQLiteDatabase db;
    private static String name;
    private String sex;
    private String birth;
    private String phone;
    private String remarks;
    private String lunarBirth;
    private byte[] icon;
    private int lunarYear;
    private int lunarMonth;
    private int lunarDay;
    private int lunarLoop;
    private int flag;
    private static int id;
    private int solarYear;
    private static int solarMonth;
    private static int solarDay;
    private EditText et_phone_number;
    private TextView tv_birth;
    private ImageView iv_phone_book;
    private String number;
    private ToggleButton tb_switch;
    private LinearLayout ll_set_date;
    private Button btn_send_message;
    private TextView tv_sendTime;
    private EditText sms_content;
    private Button btn_collection;
    private static String content;
    private static int year;
    private static int month;
    private static int day;
    private static String hour;
    private static String minute;
    private static String phone_number;
    private Calendar calendar;
    // Calendar calendar = Calendar.getInstance();
    private String a = "";
    private String b = "";
    private LayoutInflater inflater;
    private static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView tv_cancel,tv_query,tv_content;
    private LinearLayout ll_rtn;
  //  private SendStatusReciver sendStatusReciver;
    //private AlarmReceiver alarmReceiver;
    // 设置广播监听短信发送的状态
//    private IntentFilter sendFilter;
//    private IntentFilter timeFilter;
    private static final int RC_SMS_PERM = 123;
    private static String[] perms = {Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS};
    private int pend_times  = -1;
    private android.telephony.SmsManager smsManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendsms_edit);
        Bundle bundle = getIntent().getExtras();
        et_phone_number = (EditText) findViewById(R.id.tv_phone_number);
        tv_birth = (TextView) findViewById(R.id.tv_birth);
        iv_phone_book = (ImageView) findViewById(R.id.iv_phone_book);
        tb_switch = (ToggleButton) findViewById(R.id.tb_switch);
        ll_set_date = (LinearLayout) findViewById(R.id.ll_set_Date);
        btn_send_message = (Button) findViewById(R.id.btn_send_message);
        tv_sendTime = (TextView) this.findViewById(R.id.tv_time);
        sms_content = (EditText) findViewById(R.id.sms_content);
        btn_collection = (Button) findViewById(R.id.btn_collection);
        ll_rtn = (LinearLayout) findViewById(R.id.ll_rtn);
        tv_sendTime.setOnClickListener(onClickListener);
        btn_send_message.setOnClickListener(onClickListener);
        btn_collection.setOnClickListener(onClickListener);
        sharedPreferences = getSharedPreferences("alarm_record",
                Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        ll_rtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        smsManager = android.telephony.SmsManager
                .getDefault();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);
        calendar = Calendar.getInstance();
        EasyPermissions.requestPermissions(MessageWishActivity.this, "你需要赋予软件发送短信的权限！",
                RC_SMS_PERM, perms);
        tb_switch.setOnCheckedChangeListener(CheckedChangeListener);
        id = bundle.getInt("id");
        content = bundle.getString("content");
        if (content == null){
            sms_content.setText("生日快乐~~");
            sms_content.requestFocus();
            sms_content.setSelection(6);
        }else {
            sms_content.setText(content);
            sms_content.requestFocus();
            sms_content.setSelection(content.length());
        }
//        sendFilter = new IntentFilter();
//        sendFilter.addAction("SEND_SMS_ACTIONS");
//        sendStatusReciver = new SendStatusReciver();
        //registerReceiver(sendStatusReciver, sendFilter);
        // 定时发送短信
//        timeFilter = new IntentFilter();
//        timeFilter.addAction("AlarmReceiver");
//        alarmReceiver = new AlarmReceiver();
        //registerReceiver(alarmReceiver, timeFilter);
        dbHelper = new BirthInfoHelper(MessageWishActivity.this,"BirthInfo.db",null,1);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Birth",new String[]{"name","sex","birth","phone","remarks"
                        ,"lunar_birth","icon","lunar_year","lunar_month","lunar_day","lunar_loop",
                "flag","id","solar_year","solar_month","solar_day"}
                ,"id = ?",new String[]{id+""},null,null,null);
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
                solarYear = cursor.getInt(cursor.getColumnIndex("solar_year"));
                solarMonth = cursor.getInt(cursor.getColumnIndex("solar_month"));
                solarDay = cursor.getInt(cursor.getColumnIndex("solar_day"));
                lunarLoop =  cursor.getInt(cursor.getColumnIndex("lunar_loop"));
                flag = cursor.getInt(cursor.getColumnIndex("flag"));
                //  id = cursor.getInt(cursor.getColumnIndex("id"));
            }while (cursor.moveToNext());
        }
        cursor.close();
        iv_phone_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                MessageWishActivity.this.startActivityForResult(intent, 1);
            }
        });
        if (phone==null){
            phone  = bundle.getString("phone");
        }else {
            et_phone_number.setText(phone);
           // et_phone_number.setSelection(phone.length());
        }
        if (birth!=null && lunarBirth!=null){
            if (flag == 0 ){
                int month = solarMonth;
                int day = solarDay;
                tv_birth.setText(month+"月"+day+"日"+"(生日当天)");
            }
        }else if (flag == 1){
            lunarBirth = lunarBirth.substring(5);
            tv_birth.setText(lunarBirth+"(生日当天)");
        }
    }

    CompoundButton.OnCheckedChangeListener CheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {
                Log.e("===", "我是定时广播");
                // new Thread(rcp_msg_wish.this).start();
                // int hour = 0;
                // int minute = 0;
                // Calendar calendar = Calendar.getInstance();
                // calendar.set(Calendar.HOUR_OF_DAY, hour);
                // calendar.set(Calendar.MINUTE, minute);
                ll_set_date.setVisibility(View.VISIBLE);
                btn_send_message.setText("保存定时通知");
            } else {
                ll_set_date.setVisibility(View.GONE);
                // sendDate.setVisibility(View.GONE);
                btn_send_message.setText("立即发送");
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null,
                            null);
                    cursor.moveToFirst();
                    number = this.getContactPhone(cursor);
                    number.substring(3);
                    et_phone_number.requestFocus();
                    et_phone_number.setText(number);
                    Log.d("RESULT_OK","+++++++++++++++++++++++++++++"+number);
                    et_phone_number.setSelection(number.length());
                }
                break;

            default:
                break;
//            case 2:
//                Log.d("RESULT_OK","+++++++++++++++++++++++++++++");
//                Bundle bundle = data.getExtras();
//                content = bundle.getString("content");
//                sms_content.requestFocus();
//                sms_content.setText(content);
//                sms_content.setSelection(content.length());
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.tv_time:
                    Log.e("bbbbbbbbbb", "bbbbbbbbbbbb");
                    setmore_remind();
                    break;
                case R.id.btn_send_message:
                {
                    if (et_phone_number.getText().toString().length() <= 10) {
                        Toast.makeText(MessageWishActivity.this, "你确定号码有正确输入吗？", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sms_content.getText().toString().length() <= 0) {
                        Toast.makeText(MessageWishActivity.this, "输入内容为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ("立即发送".equals(btn_send_message.getText().toString())) {
                        // Intent intent = new Intent(Intent.ACTION_VIEW);
                        // // 信使压入短信内容
                        // intent.putExtra("sms_body", ed_sms.getText().toString());
                        // // 友盟统计
                        // intent.putExtra("address",
                        // ed_phone.getText().toString());
                        // // 短信界面的类型
                        // intent.setType("vnd.android-dir/mms-sms");
                        // startActivity(intent);
                        // finish();
                        Intent sendIntent = new Intent("SEND_SMS_ACTIONS");
                        pend_times++;
                        PendingIntent pi = PendingIntent.getBroadcast(
                                MessageWishActivity.this, pend_times, sendIntent, 0);
                        if(EasyPermissions.hasPermissions(MessageWishActivity.this,perms)){
                            smsManager.sendTextMessage(et_phone_number.getText().toString(),
                                    null, sms_content.getText().toString(), pi, null);
                            Log.d("permissions","------------------------------------");
                        }else {
                            EasyPermissions.requestPermissions(MessageWishActivity.this, "你需要赋予软件发送短信的权限！",
                                    RC_SMS_PERM, perms);
                            Log.d("permissions","===================================");
                        }
                    } else {

                         year = solarYear;
                         month = solarMonth;
                         day = solarDay;
                         hour = sharedPreferences.getString("hour", null);
                         minute = sharedPreferences.getString("minute", null);
                        Log.d("sharedPreferences",sharedPreferences.getString("hour", null));
                        Log.d("sharedPreferences",sharedPreferences.getString("minute", null));
                       final Dialog  dialog = new Dialog(MessageWishActivity.this, R.style.dialog);
                        inflater = LayoutInflater.from(MessageWishActivity.this);
                        View view = inflater.inflate(R.layout.query_delete_dialog,null);
                        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                        tv_query = (TextView) view.findViewById(R.id.tv_delete);
                        tv_content = (TextView) view.findViewById(R.id.tv_content);
//                        tv_content.setText("你确定要在" + year + "年" + month + "月"
//                                + day + "日" + hour + "时" + minute + "分发送这条祝福短信吗？");
                        tv_content.setText("确认定时通知？");
                        // 设置dialog没有title
                        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        Window window = dialog.getWindow();
                        // 可以在此设置显示动画
                        WindowManager.LayoutParams wl = window.getAttributes();
                        wl.x = 0;
                        //  wl.y = window.getWindowManager().getDefaultDisplay().getHeight();
                        // 以下这两句是为了保证按钮可以水平满屏
                        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        // 设置显示位置
                        dialog.onWindowAttributesChanged(wl);
                        dialog.show();
//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                                MessageWishActivity.this);
//                        // alertDialog.show();
//                        alertDialog.setTitle("提醒消息");
//                        alertDialog.setMessage("你确定要在" + year + "年" + month + "月"
//                                + day + "日" + hour + "时" + minute + "分发送这条祝福短信吗？");
//                        alertDialog.setNegativeButton("取消",
//                                new DialogInterface.OnClickListener() {
//
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        // TODO Auto-generated method stub
//                                        // finish();
//                                    }
//                                });
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
//                        alertDialog.setPositiveButton("确定",
//                                new DialogInterface.OnClickListener() {
//
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        Log.e("=============", tv_sendTime
//                                                .getText().toString());
//                                        dingshiSendSMS();
//                                        Intent intent = new Intent(
//                                                MessageWishActivity.this,
//                                                MainActivity.class);
//                                        MessageWishActivity.this.startActivity(intent);
//                                    }
//                                });
                        tv_query.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dingshiSendSMS();
                                Intent intent = new Intent(
                                        MessageWishActivity.this,
                                        MainActivity.class);
                                MessageWishActivity.this.startActivity(intent);
                            }
                        });
                        dialog.show();
                    }
                }
                break;
                case R.id.btn_collection:
                    Intent intent = new Intent(MessageWishActivity.this,CollectionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("phone",phone);
                    bundle.putInt("id",id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }

        }
    };

    /***
     * 得到Sms_info传过来的短信内容
     *
     */
    public void getSmsInfo() {

        smsInfo = content;
      //  et_phone_number.setText(phone);
      //  brithID = sms.getIntExtra("brithID", -1);
        if (smsInfo != null) {
            sms_content.setText(smsInfo);
        }
    }

    public void dingshiSendSMS() {
        // Intent intent = new Intent();
        // intent.putExtra("haoma", ed_phone.getText().toString());
        // intent.putExtra("neirong", ed_sms.getText().toString());
        // PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        // // 设置一个PendingIntent对象，发送广播
        // AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // // 获取AlarmManager对象
        // am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        long firstime = SystemClock.elapsedRealtime();
        Log.d("My_test", "---------------" + firstime);
        AlarmManager aManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Intent intent = new Intent(rcp_msg_wish.this, AlarmReceiver.class);
        // intent.setAction("AlarmReceiver");
        Intent intent = new Intent("AlarmReceiver");
        pend_times++;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MessageWishActivity.this, pend_times, intent, 0);
        aManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
                60 * 1000, pendingIntent);
    }

//    public void getDateTime() {
//        int year = 0;
//        int month = 0;
//        int day = -1;
//        int hours = -1;
//        int min = -1;
//        hours = sharedPreferences.getInt("hours", 00);
//        min = sharedPreferences.getInt("min", 00);
//        System.out.println(calendar.getTime().getYear());
//        month = solarMonth;
//        day = solarDay;
//        year = OtherUtil.getCurYear();
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.MONTH, month - 1);// 也可以填数字，0-11,一月为0
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        if (hours != -1) {
//            calendar.set(Calendar.HOUR_OF_DAY, hours);
//            calendar.set(Calendar.MINUTE, min);
//            calendar.set(Calendar.SECOND, 0);
//        } else {
//            calendar.set(Calendar.HOUR_OF_DAY, 12);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//        }
//        Log.e("--", calendar.getTimeInMillis() + "---" + System.currentTimeMillis());
//
//        Log.e("mmmmmmmmmm", month + "-" + day + "-" + hours + "-" + min);
//
//        editor.putString("year", year + "");
//        editor.putString("month", month + "");
//        editor.putString("day", day + "");
//        editor.commit();
//        tv_birth.setText(year + "年" + month + "月" + day + "日(生日当天)");
//        tv_sendTime.setText("0" + hours + ":0" + min);
//    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    // 定时发送短信的广播
    public static class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // SharedPreferences sharedPreferences
        // =context.getSharedPreferences(
        // "alarm_record",Activity.MODE_PRIVATE);
        // String hour =String.valueOf(Calendar.getInstance().get(
        // Calendar.HOUR_OF_DAY));
        // String minute =String.valueOf(Calendar.getInstance().get(
        // Calendar.MINUTE));
         Calendar receiverCalendar = Calendar.getInstance();
        // year = sharedPreferences.getString("year", null);
         year = receiverCalendar.get(Calendar.YEAR);
         month = solarMonth;
         day = solarDay;
         hour = sharedPreferences.getString("hour", null);
         minute = sharedPreferences.getString("minute", null);
         phone_number = sharedPreferences.getString("phone_number", null);
         content = sharedPreferences.getString("content", null);
        Log.d("My_test",
                "---------------" + year + "------"
                        + (receiverCalendar.get(Calendar.YEAR)));
        Log.d("My_test",
                "---------------" + month + "------"
                        + (receiverCalendar.get(Calendar.MONTH) + 1));
        Log.d("My_test",
                "---------------" + day + "------"
                        + (receiverCalendar.get(Calendar.DAY_OF_MONTH)));
        Log.d("My_test",
                "---------------" + hour + "------"
                        + (receiverCalendar.get(Calendar.HOUR_OF_DAY)));
        Log.d("My_test",
                "---------------" + minute + "------"
                        + (receiverCalendar.get(Calendar.MINUTE)));
        Log.d("My_test", "---------------" + phone_number);
        Log.d("My_test", "---------------" + content);
        if (year==(receiverCalendar.get(Calendar.YEAR))
                && month==((receiverCalendar.get(Calendar.MONTH) + 1))
                && day==(receiverCalendar.get(Calendar.DAY_OF_MONTH))
                && hour.equals(receiverCalendar.get(Calendar.HOUR_OF_DAY) + "")
                && minute.equals(receiverCalendar.get(Calendar.MINUTE) + "")) {// 判断是否为空，然后通过创建，
            // MediaPlayer mediaPlayer =MediaPlayer.create(context,
            // R.raw.a);
            // mediaPlayer.start();// 开始 ;
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            Intent intent1 =new Intent(context,MessageWishActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id",id);
            bundle.putString("content",content);
            intent1.putExtras(bundle);
            PendingIntent pi = PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentTitle("好友生日提醒");
            builder.setContentText("今天是好友"+name+"的生日！");
            builder.setSmallIcon(R.mipmap.app_icon);
            builder.setContentIntent(pi);
            Notification notification = builder.getNotification();
            notification.defaults = Notification.DEFAULT_ALL;
            manager.notify(1,notification);
           // sendMsg(phone_number, content,context);
           // Toast.makeText(context, "短信已经发送成功", Toast.LENGTH_LONG).show();
        }

    }

}

    private static void sendMsg(String number, String message,Context context) {
        SmsManager smsManager = SmsManager.getDefault();
        if(EasyPermissions.hasPermissions(context,perms)){
            smsManager.sendTextMessage(number, null, message, null, null);
            Log.d("permissions","------------------------------------");
        }else {
            EasyPermissions.requestPermissions(context, "你需要赋予软件发送短信的权限！",
                    RC_SMS_PERM, perms);
            Log.d("permissions","===================================");
        }
    }


// 监听短信发送成功与否
public static class SendStatusReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() == RESULT_OK ) {
            // 短信发送成功
            Toast.makeText(context, "短信发送成功！", Toast.LENGTH_SHORT)
                    .show();
            Log.d("REQUEST_CODE", String.valueOf(getResultCode())+"============================");
            Intent intent1 = new Intent(context,MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        } else {
            // 短信发送失败
            Log.d("REQUEST_CODE", String.valueOf(getResultCode())+"-------------------");
            Toast.makeText(context, "短信发送失败!", Toast.LENGTH_SHORT)
                    .show();
        }

    }

}
    /***
     *
     * 设置提醒方式
     *
     */
    public void setmore_remind() {
        LayoutInflater inflater = LayoutInflater.from(MessageWishActivity.this);
        View timepickerview = inflater.inflate(R.layout.select_time, null);
        ScreenInfo screenInfo = new ScreenInfo(MessageWishActivity.this);
        final WheelMain wheelMain = new WheelMain(timepickerview);
        wheelMain.screenheight = screenInfo.getHeight();
        calendar.setTimeInMillis(System.currentTimeMillis());
        wheelMain.showHours(calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        final Dialog dialog1 = new AlertDialog.Builder(this).setView(
                timepickerview).show();
        Window window = dialog1.getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle); // 添加动画

        Button btn = (Button) timepickerview
                .findViewById(R.id.btn_datetime_sure);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (wheelMain.getHours() <= 9) {
                    a = "0" + wheelMain.getHours();
                } else {
                    a = wheelMain.getHours() + "";
                }
                if (wheelMain.getMin() <= 9) {
                    b = "0" + wheelMain.getMin();
                } else {
                    b = wheelMain.getMin() + "";
                }
                String info = a + ":" + b;
                tv_sendTime.setText(info);
                editor.putString("hour", wheelMain.getHours() + "");
                editor.putString("minute", wheelMain.getMin() + "");
                editor.putString("phone_number", et_phone_number.getText().toString());
                editor.putString("content", sms_content.getText().toString());
                editor.commit();
                calendar.set(Calendar.HOUR_OF_DAY, wheelMain.getHours());
                calendar.set(Calendar.MINUTE, wheelMain.getMin());
                dialog1.dismiss();
            }
        });
    }

//    public void dataInit() {
//        getDateTime();
//    };
    private String getContactPhone(Cursor cursor) {
        // TODO Auto-generated method stub
        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String result = "";
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    String phoneNumber = phone.getString(index);
                    result = phoneNumber;
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        getSmsInfo();
        //dataInit();
        smsManager = android.telephony.SmsManager
                .getDefault();
        Log.d("onResume","onResume--------");
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        ActivityMeg.getInstance().removeActivity(this);
//        unregisterReceiver(sendStatusReciver);
//        unregisterReceiver(alarmReceiver);
        super.onDestroy();
    }
}

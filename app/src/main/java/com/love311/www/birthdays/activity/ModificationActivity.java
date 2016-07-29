package com.love311.www.birthdays.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.sql.BirthInfoHelper;
import com.love311.www.birthdays.view.GregorianLunarCalendarView;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.github.xhinliang.lunarcalendar.LunarCalendar;

/**
 * Created by Administrator on 2016/5/24.
 */
public class ModificationActivity extends AutoLayoutActivity{

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private LinearLayout ll_take_pic, ll_album, ll_cancel;
    private BirthInfoHelper dbHelper;
    private SQLiteDatabase db;
    private String name,sex,birth,phone,remarks,lunarBirth;
    private byte[] icon;
    private int lunarYear,lunarMonth,lunarDay,lunarLoop,flag,id;
    private EditText mEditName,mEditPhone,mEditRemarks;
    private TextView mSaveInfo;
    private TextView mTextBirth;
    private RadioGroup group;
    private ImageView iv_solar;
    private RadioButton boy,girl;
    private Bitmap bitmap;
    private ByteArrayInputStream bais;
    private ImageView iv_icon;
    private int BIRTH_DAY_CHOOSE = 0;
    private int[] solarToLunar;
    private int FLAG = 0 ;
    private byte[] args;
    private Dialog dialog;
    private GregorianLunarCalendarView.CalendarData calendarData;
    private LunarCalendar lunarCalender;
    private Button solar_btn,lunar_btn;
    private TextView tv_query;
    private GregorianLunarCalendarView mView;
    private ImageView iv_rtn;
    File tempFile = new File(Environment.getExternalStorageDirectory()
            + "/brithPhoto/", getPhotoFileName());
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modification_birth);
        mSaveInfo = (TextView) findViewById(R.id.save_info);
        mEditName = (EditText) findViewById(R.id.et_name);
        mTextBirth = (TextView) findViewById(R.id.tv_birth);
        mEditPhone = (EditText) findViewById(R.id.et_phone);
        mEditRemarks = (EditText) findViewById(R.id.et_remarks);
        group = (RadioGroup) this.findViewById(R.id.add_radioGourp);
        boy = (RadioButton) findViewById(R.id.add_brith_nan);
        girl = (RadioButton) findViewById(R.id.add_brith_nv);
        iv_solar = (ImageView) findViewById(R.id.iv_solar);
        iv_icon = (ImageView) findViewById(R.id.user_icon);
        iv_rtn = (ImageView) findViewById(R.id.iv_rtn);
        iv_rtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        solarToLunar = new int[4];
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        dbHelper = new BirthInfoHelper(ModificationActivity.this,"BirthInfo.db",null,1);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Birth",new String[]{"name","sex","birth","phone","remarks"
        ,"lunar_birth","icon","lunar_year","lunar_month","lunar_day","lunar_loop","flag","id"}
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
                lunarLoop =  cursor.getInt(cursor.getColumnIndex("lunar_loop"));
                flag = cursor.getInt(cursor.getColumnIndex("flag"));
              //  id = cursor.getInt(cursor.getColumnIndex("id"));
            }while (cursor.moveToNext());
        }
        cursor.close();
        mTextBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dialog","----------------------------");
                dialog = new Dialog(ModificationActivity.this, R.style.dialog);
                // 选择的dialog，以及其上的控件
                View view = getLayoutInflater().inflate(R.layout.date_dialog, null);
                solar_btn = (Button) view.findViewById(R.id.solar);
                lunar_btn = (Button) view.findViewById(R.id.lunar);
                tv_query = (TextView) view.findViewById(R.id.query);
                mView = (GregorianLunarCalendarView) view.findViewById(R.id.my_view);
                //初始化自定义对话框
                mView.initConfigs(Calendar.getInstance(), true);
                mView.setGregorian(true);
                solar_btn.setTextColor(Color.BLUE);
                FLAG = 0;
                //阳历
                solar_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mView.setGregorian(true);
                        solar_btn.setTextColor(Color.BLUE);
                        lunar_btn.setTextColor(Color.BLACK);
                        FLAG = 0;
                    }
                });
                //农历
                lunar_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mView.setGregorian(false);
                        lunar_btn.setTextColor(Color.BLUE);
                        solar_btn.setTextColor(Color.BLACK);
                        FLAG = 1;
                    }
                });
                //确认按钮
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendarData = mView.getCalendarData();
                        Log.d("AddBirthDay", "calendarData G --> y:" + calendarData.getCalendar().get(Calendar.YEAR));
                        Log.d("AddBirthDay", "calendarData G --> m:" + (calendarData.getCalendar().get(Calendar.MONTH) + 1));
                        Log.d("AddBirthDay", "calendarData G --> d:" + calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));

//                        Log.d("AddBirthDay", "calendarData L --> y:" + calendarData.getCalendar().get(ChineseCalendar.CHINESE_YEAR));
//                        Log.d("AddBirthDay", "calendarData L --> m:" + calendarData.getCalendar().get(ChineseCalendar.CHINESE_MONTH));
//                        Log.d("AddBirthDay", "calendarData L --> d:" + calendarData.getCalendar().get(ChineseCalendar.CHINESE_DATE));
                        if (FLAG == 0){
                            lunarCalender = LunarCalendar.getInstance(calendarData.getCalendar().get(Calendar.YEAR), (calendarData.getCalendar().get(Calendar.MONTH) + 1), calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                            mTextBirth.setText(calendarData.getCalendar().get(Calendar.YEAR)+"-"+(calendarData.getCalendar().get(Calendar.MONTH) + 1)+"-"+calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                            iv_solar.setImageResource(R.mipmap.solar_icon);
                        }else if (FLAG == 1){
                            solarToLunar = com.love311.www.birthdays.utils.LunarCalendar.solarToLunar(calendarData.getCalendar().get(Calendar.YEAR),
                                    (calendarData.getCalendar().get(Calendar.MONTH) + 1),
                                    calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                            Log.d("AddBirthDay",solarToLunar[0]+"--------------"+solarToLunar[1]+"--------------"+solarToLunar[2]+"--------------"+solarToLunar[3]+"--------------");
                            lunarCalender = LunarCalendar.getInstance(calendarData.getCalendar().get(Calendar.YEAR), (calendarData.getCalendar().get(Calendar.MONTH) + 1), calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                            //mTextBirth.setText(calendarData.getCalendar().get(ChineseCalendar.CHINESE_YEAR)+"-"+calendarData.getCalendar().get(ChineseCalendar.CHINESE_MONTH)+"-"+calendarData.getCalendar().get(ChineseCalendar.CHINESE_DATE));
                            iv_solar.setImageResource(R.mipmap.lunar_icon);
                            mTextBirth.setText(lunarCalender.getFullLunarStr());
                        }
                        BIRTH_DAY_CHOOSE =2;
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Window window = dialog.getWindow();
                // 可以在此设置显示动画
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.x = 0;
                wl.y = getWindowManager().getDefaultDisplay().getHeight();
                // 以下这两句是为了保证按钮可以水平满屏
                wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
                wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                // 设置显示位置
                dialog.onWindowAttributesChanged(wl);
                dialog.show();
            }
        });
        iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ModificationActivity.this, R.style.dialog);
                // 选择的dialog，以及其上的控件
                View view = getLayoutInflater().inflate(R.layout.bottom_icon_dialog, null);
                ll_take_pic = (LinearLayout) view.findViewById(R.id.ll_take_pic);
                ll_album = (LinearLayout) view.findViewById(R.id.ll_album);
                ll_cancel = (LinearLayout) view.findViewById(R.id.ll_cancel);
                //调用系统拍照
                ll_take_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraintent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(tempFile));
                        startActivityForResult(cameraintent,
                                PHOTO_REQUEST_TAKEPHOTO);
                    }
                });
                //调用系统相册
                ll_album.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                        getAlbum.setType("image/*");
                        startActivityForResult(getAlbum, PHOTO_REQUEST_GALLERY);
                    }
                });


                //取消
                ll_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // 设置dialog没有title
                dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Window window = dialog.getWindow();
                // 可以在此设置显示动画
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.x = 0;
                wl.y = getWindowManager().getDefaultDisplay().getHeight();
                // 以下这两句是为了保证按钮可以水平满屏
                wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
                wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                // 设置显示位置
                dialog.onWindowAttributesChanged(wl);
                dialog.show();
            }
        });
        mSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditName.getText().toString()==null || mEditName.getText().toString().equals("")){
                    Toast.makeText(ModificationActivity.this,"亲，请填写好友名称！",Toast.LENGTH_SHORT).show();
                }else if (group.getCheckedRadioButtonId()!=R.id.add_brith_nan && group.getCheckedRadioButtonId()!=R.id.add_brith_nv){
                    Toast.makeText(ModificationActivity.this,"亲，请填写好友性别！",Toast.LENGTH_SHORT).show();
                }else if (BIRTH_DAY_CHOOSE == 1){
                    Toast.makeText(ModificationActivity.this,"亲，请填写好友生日！",Toast.LENGTH_SHORT).show();
                }else if (mEditPhone.getText().toString().length()!=11){
                    Toast.makeText(ModificationActivity.this,"亲，请填写正确的电话号码！",Toast.LENGTH_SHORT).show();
                }else {
                    db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("name",mEditName.getText().toString());
                    values.put("sex",(group.getCheckedRadioButtonId() == R.id.add_brith_nan) ? "他"
                            : "她");
                    if (BIRTH_DAY_CHOOSE==2){
                        values.put("birth",calendarData.getCalendar().get(Calendar.YEAR)+"-"+(calendarData.getCalendar().get(Calendar.MONTH) + 1)+"-"+calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                        values.put("lunar_birth",lunarCalender.getFullLunarStr());
                        values.put("lunar_year",solarToLunar[0]);
                        values.put("lunar_month",solarToLunar[1]);
                        values.put("lunar_day",solarToLunar[2]);
                        values.put("lunar_loop",solarToLunar[3]);
                    }else if(BIRTH_DAY_CHOOSE == 0){
                        values.put("birth",birth);
                        values.put("lunar_birth",lunarBirth);
                        values.put("lunar_year",lunarYear);
                        values.put("lunar_month",lunarMonth);
                        values.put("lunar_day",lunarDay);
                        values.put("lunar_loop",lunarLoop);
                    }
                    values.put("phone",mEditPhone.getText().toString());
                    values.put("flag",FLAG);
                    values.put("remarks",mEditRemarks.getText().toString());
                    values.put("icon", args);
                    db.update("Birth",values,"id = ?",new String[]{id+""});
                    values.clear();
                    Intent intent = new Intent(ModificationActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        mEditName.setText(name);
        mEditName.setSelection(name.length());
        if (sex.equals("他")){
            boy.setChecked(true);
        }else {
            girl.setChecked(true);
        }
        if (flag == 0){
            mTextBirth.setText(birth);
            iv_solar.setImageResource(R.mipmap.solar_icon);
        }else {
            mTextBirth.setText(lunarBirth);
            iv_solar.setImageResource(R.mipmap.lunar_icon);
        }
        mEditPhone.setText(phone);
        mEditRemarks.setText(remarks);
        if (icon != null) {
            bais = new ByteArrayInputStream(icon);
            bitmap = BitmapFactory.decodeStream(bais);
            iv_icon.setImageBitmap(bitmap);
        } else if (sex.equals("他")) {
           iv_icon.setImageResource(R.mipmap.boy);
        } else {
            iv_icon.setImageResource(R.mipmap.girl);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
                startPhotoZoom(Uri.fromFile(tempFile));
                break;
            case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
                // 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                if (data != null) {
                    System.out.println("11================");
                    startPhotoZoom(data.getData());
                } else {
                    System.out.println("================");
                }
                break;
            // 返回的结果
            case PHOTO_REQUEST_CUT:
                if (data != null)
                    setPicToView(data);
                //sentPicToNext(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //设置图片到界面上
    private void setPicToView(Intent data) {

        Bundle bundle = data.getExtras();
        Log.d("Buddle",data.getExtras()+"");
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            Log.d("Bitmap",photo+"");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);//
            // Drawable drawable = new BitmapDrawable(photo);
            iv_icon.setImageBitmap(photo);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ((BitmapDrawable) iv_icon.getDrawable()).getBitmap().compress(
                        Bitmap.CompressFormat.PNG, 75, baos);//压缩为PNG格式,100表示跟原图大小一样
                args = baos.toByteArray();
                baos.close();;
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }
    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        System.out.println("22================");
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
}

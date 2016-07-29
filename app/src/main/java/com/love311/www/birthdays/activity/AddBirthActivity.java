package com.love311.www.birthdays.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.sql.BirthInfoHelper;
import com.love311.www.birthdays.view.GregorianLunarCalendarView;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.github.xhinliang.lunarcalendar.LunarCalendar;

/**
 * Created by Administrator on 2016/5/21.
 */
public class AddBirthActivity extends AutoLayoutActivity{

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private ImageView mUserIcon;
    private LinearLayout ll_take_pic, ll_album, ll_cancel;
    File tempFile = new File(Environment.getExternalStorageDirectory()
            + "/brithPhoto/", getPhotoFileName());
    private Dialog dialog;
    private BirthInfoHelper dbHelper;
    private SQLiteDatabase db;
    private TextView mSaveInfo;
    private EditText mEditName,mEditPhone,mEditRemarks;
    private RadioGroup group;
    private TextView mTextBirth;
    private TextView tv_query;
    private Button solar_btn,lunar_btn;
    private GregorianLunarCalendarView mView;
    private ImageView iv_solar;
    private int FLAG = 0 ;
    private GregorianLunarCalendarView.CalendarData calendarData;
    private LunarCalendar lunarCalender;
    private int[] solarToLunar;
    private int BIRTH_DAY_CHOOSE = 1;
    private byte[] args;
    private Calendar calendar;
    private ImageView iv_rtn;
    private Bundle bundle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_birth);
        mUserIcon = (ImageView) findViewById(R.id.user_icon);
        mSaveInfo = (TextView) findViewById(R.id.save_info);
        mEditName = (EditText) findViewById(R.id.et_name);
        mTextBirth = (TextView) findViewById(R.id.tv_birth);
        mEditPhone = (EditText) findViewById(R.id.et_phone);
        mEditRemarks = (EditText) findViewById(R.id.et_remarks);
        group = (RadioGroup) this.findViewById(R.id.add_radioGourp);
        iv_solar = (ImageView) findViewById(R.id.iv_solar);
        iv_rtn = (ImageView) findViewById(R.id.iv_rtn);
        solarToLunar = new int[4];
        calendar = Calendar.getInstance();
        dbHelper = new BirthInfoHelper(this,"BirthInfo.db",null,1);
        iv_rtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mUserIcon.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog = new Dialog(AddBirthActivity.this, R.style.dialog);
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

        mTextBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("dialog","----------------------------");
                dialog = new Dialog(AddBirthActivity.this, R.style.dialog);
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
        mSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditName.getText().toString()==null || mEditName.getText().toString().equals("")){
                    Toast.makeText(AddBirthActivity.this,"亲，请填写好友名称！",Toast.LENGTH_SHORT).show();
                }else if (group.getCheckedRadioButtonId()!=R.id.add_brith_nan && group.getCheckedRadioButtonId()!=R.id.add_brith_nv){
                    Toast.makeText(AddBirthActivity.this,"亲，请填写好友性别！",Toast.LENGTH_SHORT).show();
                }else if (BIRTH_DAY_CHOOSE == 1){
                    Toast.makeText(AddBirthActivity.this,"亲，请填写好友生日！",Toast.LENGTH_SHORT).show();
                }else if (mEditPhone.getText().toString().length()!=11){
                    Toast.makeText(AddBirthActivity.this,"亲，请填写正确的电话号码！",Toast.LENGTH_SHORT).show();
                }else {
                    db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("name",mEditName.getText().toString());
                    values.put("sex",(group.getCheckedRadioButtonId() == R.id.add_brith_nan) ? "他"
                            : "她");
                    if ((calendar.get(Calendar.MONTH)+1)>=(calendarData.getCalendar().get(Calendar.MONTH) + 1)){
                        if (calendar.get(Calendar.DAY_OF_MONTH)>calendarData.getCalendar().get(Calendar.DAY_OF_MONTH)){
                            values.put("birth",(calendarData.getCalendar().get(Calendar.YEAR)+1)+"-"+(calendarData.getCalendar().get(Calendar.MONTH) + 1)+"-"+calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                            values.put("solar_year",calendarData.getCalendar().get(Calendar.YEAR)+1);
                        }else {
                            values.put("birth",calendarData.getCalendar().get(Calendar.YEAR)+"-"+(calendarData.getCalendar().get(Calendar.MONTH) + 1)+"-"+calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                            values.put("solar_year",calendarData.getCalendar().get(Calendar.YEAR));
                        }
                    }else {
                        values.put("birth",calendarData.getCalendar().get(Calendar.YEAR)+"-"+(calendarData.getCalendar().get(Calendar.MONTH) + 1)+"-"+calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                        values.put("solar_year",calendarData.getCalendar().get(Calendar.YEAR));
                    }
                    values.put("solar_month",(calendarData.getCalendar().get(Calendar.MONTH) + 1));
                    values.put("solar_day",calendarData.getCalendar().get(Calendar.DAY_OF_MONTH));
                    values.put("lunar_birth",lunarCalender.getFullLunarStr());
                    values.put("lunar_year",solarToLunar[0]);
                    values.put("lunar_month",solarToLunar[1]);
                    values.put("lunar_day",solarToLunar[2]);
                    values.put("lunar_loop",solarToLunar[3]);
                    values.put("phone",mEditPhone.getText().toString());
                    values.put("flag",FLAG);
                    values.put("remarks",mEditRemarks.getText().toString());
                    values.put("icon", args);
                    db.insert("Birth",null,values);
                    values.clear();
                    Intent intent = new Intent(AddBirthActivity.this,MainActivity.class);
                    startActivity(intent);
                    AddBirthActivity.this.finish();
                }
            }
        });
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
                    Log.d("AddBirthActivity",data.toString());
                    setPicToView(data);
                //sentPicToNext(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //设置图片到界面上
    private void setPicToView(Intent data) {

        bundle = data.getExtras();
        Log.d("Buddle",data.getExtras()+"");
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            Log.d("Bitmap",photo+"");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);//
            // Drawable drawable = new BitmapDrawable(photo);
            mUserIcon.setImageBitmap(photo);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ((BitmapDrawable) mUserIcon.getDrawable()).getBitmap().compress(
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
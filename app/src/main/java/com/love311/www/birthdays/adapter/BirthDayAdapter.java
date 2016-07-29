package com.love311.www.birthdays.adapter;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.love311.www.birthdays.R;
import com.love311.www.birthdays.sql.BirthInfoHelper;
import com.love311.www.birthdays.utils.LunarCalendar;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/22.
 */
public class BirthDayAdapter extends BaseSwipeAdapter {

    private List<Map<String, Object>> mList;
    private LayoutInflater mInflater;
    private int[] lunar = new int[4];
    private Bitmap bitmap;
    private ByteArrayInputStream bais;
    private Context mContext;
    private BirthInfoHelper dbHelper;
    private Dialog dialog;
    private LayoutInflater inflater;
    private TextView tv_cancel,tv_delete;
    public BirthDayAdapter(Context context, List<Map<String, Object>> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.swipe, null);
        return view;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        {
            final ViewHolder viewHolder;
            viewHolder = new ViewHolder();
            dbHelper = new BirthInfoHelper(mContext,"BirthInfo.db",null,1);
            //convertView = mInflater.inflate(R.layout.swipe, null);
            viewHolder.swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
            //set show mode.
            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            //set drag edge.
            //viewHolder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
            viewHolder.ll_delete = (LinearLayout) convertView.findViewById(R.id.ll_delete);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.user_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.tv_birth = (TextView) convertView.findViewById(R.id.tv_birth);
            viewHolder.tv_birth_days = (TextView) convertView.findViewById(R.id.birth_days);
            viewHolder.lunar_icon = (ImageView) convertView.findViewById(R.id.lunar_icon);
            viewHolder.tv_sex = (TextView) convertView.findViewById(R.id.tv_sex);
            convertView.setTag(viewHolder);
            //删除按钮的监听事件
            viewHolder.ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(mContext, R.style.dialog);
                    inflater = LayoutInflater.from(mContext);
                    View view = inflater.inflate(R.layout.query_delete_dialog,null);
                    tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                    tv_delete = (TextView) view.findViewById(R.id.tv_delete);
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
                    tv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = (int) mList.get(position).get("id");
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.delete("Birth","id=?",new String[]{id+""});
                            mList.remove(position);
                            viewHolder.swipeLayout.close();
                            dialog.dismiss();
                            notifyDataSetChanged();
                        }
                    });
                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            viewHolder.swipeLayout.close();
                        }
                    });
                }
            });
            viewHolder.tv_name.setText((CharSequence) mList.get(position).get("name"));
            if (mList.get(position).get("icon") != null) {
                bais = new ByteArrayInputStream((byte[]) mList.get(position).get("icon"));
                bitmap = BitmapFactory.decodeStream(bais);
                viewHolder.iv_icon.setImageBitmap(bitmap);
            } else if (mList.get(position).get("sex").equals("他")) {
                viewHolder.iv_icon.setImageResource(R.mipmap.male);
            } else {
                viewHolder.iv_icon.setImageResource(R.mipmap.girl);
            }
            Log.d("BirthDayAdapter", mList.get(position).get("flag") + "");
            if (mList.get(position).get("flag").equals(0)) {
                int solarMonth = (int) mList.get(position).get("solar_month");
                int solarDay = (int) mList.get(position).get("solar_day");
                viewHolder.tv_birth.setText(solarMonth+"月"+solarDay+"日");
                viewHolder.tv_birth_days.setText(getSolarBirthday((String) mList.get(position).get("birth")));
                viewHolder.lunar_icon.setImageResource(R.mipmap.solar_icon);
                viewHolder.tv_sex.setText("离" + mList.get(position).get("sex") + "的公历生日");
            } else if (mList.get(position).get("flag").equals(1)) {
                String birth = (String) mList.get(position).get("lunar_birth");
                birth = birth.substring(5);
                viewHolder.tv_birth.setText(birth);
                viewHolder.lunar_icon.setImageResource(R.mipmap.lunar_icon);
                viewHolder.tv_sex.setText("离" + mList.get(position).get("sex") + "的农历生日");
                Calendar cal = Calendar.getInstance();
                int yearNow = cal.get(Calendar.YEAR);// 获得当前年份
                Log.d("____________________", mList.get(position).get("lunar_month") + "");
                int month = (int) mList.get(position).get("lunar_month");
                int day = (int) mList.get(position).get("lunar_day");
                if (mList.get(position).get("lunar_loop").equals(0)) {
                    lunar = LunarCalendar.lunarToSolar(yearNow, month, day, false);
                } else {
                    lunar = LunarCalendar.lunarToSolar(yearNow, month, day, true);
                    //闰月就加一个月
                    lunar[1] = lunar[1] + 1;
                }
                String lunar_date = lunar[0] + "-" + lunar[1] + "-" + lunar[2];
                Log.d("BirthDayAdapter", lunar_date + "++++++++++++");
                viewHolder.tv_birth_days.setText(getSolarBirthday(lunar_date));
                Log.d("BirthDayAdapter", (String) mList.get(position).get("lunar_birth"));
            }
        }
    }

   public class ViewHolder {
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_birth;
        private TextView tv_birth_days;
        private ImageView lunar_icon;
        private TextView tv_sex;
        private LinearLayout ll_delete;
        private SwipeLayout swipeLayout;
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

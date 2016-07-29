package com.love311.www.birthdays.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/5/21.
 */
public class BirthInfoHelper extends SQLiteOpenHelper{
    public static  final String CREATE_BIRTH = "create table Birth (id integer primary key autoincrement,name text,"
            +"sex text,"
            +"solar_year integer,"
            +"solar_month integer,"
            +"solar_day integer,"
            +"birth text,"
            +"phone text,"
            +"lunar_birth text,"
            +"lunar_year integer,"
            +"lunar_month integer,"
            +"lunar_day integer,"
            +"lunar_loop integer,"
            +"flag integer,"
            +"icon blob,"
            +"remarks text)";

    private Context mContext;
    public BirthInfoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BIRTH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

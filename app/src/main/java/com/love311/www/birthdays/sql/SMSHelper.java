package com.love311.www.birthdays.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/5/21.
 */
public class SMSHelper extends SQLiteOpenHelper{
    public static  final String CREATE_SMS = "create table Sms (id integer primary key autoincrement,"
            +"content text,"
            +"status integer,"
            +"flag integer)";

    private Context mContext;
    public SMSHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SMS);
        Toast.makeText(mContext,"succed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package ru.imunit.maquizdb.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by imunit on 14.11.15.
 */
public class BlackDirsTable {

    public static final String TABLE_NAME = "black_dirs";
    public static final String COLUMN_PATH = "path";

    public static final String TABLE_CREATE =
            "create table if not exists `" + TABLE_NAME + "` ( `"
                    + COLUMN_PATH + "` text primary key"
                    + ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(int oldVersion, int newVersion, SQLiteDatabase db) {

    }

}

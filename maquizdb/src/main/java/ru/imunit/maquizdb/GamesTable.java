package ru.imunit.maquizdb;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by imunit on 29.09.2015.
 */
public class GamesTable {

    public static final String TABLE_NAME = "games";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_AVG_GUESS_TIME = "avg_guess_time";
    public static final String COLUMN_BEST_GUESS_TIME = "best_guess_time";

    public static final String TABLE_CREATE =
            "create table if not exists `" + TABLE_NAME + "` ( `"
            + COLUMN_ID + "` integer primary key, `"
            + COLUMN_SCORE + "` integer not null, `"
            + COLUMN_AVG_GUESS_TIME + "` integer not null, `"
            + COLUMN_BEST_GUESS_TIME + "` integer not null "
            + ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(int oldVersion, int newVersion, SQLiteDatabase db) {
        Log.w(GamesTable.class.getName(), String.format
                ("Upgrading table %1 from v.%2 to v.%3",
                        TABLE_NAME, oldVersion, newVersion));
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

}

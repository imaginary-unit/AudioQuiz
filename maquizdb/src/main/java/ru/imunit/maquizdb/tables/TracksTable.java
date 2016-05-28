package ru.imunit.maquizdb.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by imunit on 29.09.2015.
 */
public class TracksTable {

    public static final String TABLE_NAME = "tracks";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_IS_REMOTE = "is_remote";
    public static final String COLUMN_GUESS = "guess";
    public static final String COLUMN_CORRECT_GUESS = "correct_guess";
    public static final String COLUMN_IS_BLACKLISTED = "is_blacklisted";

//
//    public static final String[] allColumns =
//            { COLUMN_NAME, COLUMN_ARTIST, COLUMN_ALBUM, COLUMN_YEAR,
//              COLUMN_URI, COLUMN_IS_REMOTE, COLUMN_GUESS, COLUMN_CORRECT_GUESS
//            };

    private static final String TABLE_CREATE =
            "create table if not exists `" + TABLE_NAME + "` ( `"
                    + COLUMN_NAME + "` text not null, `"
                    + COLUMN_ARTIST + "` text not null, `"
                    + COLUMN_URI + "` text not null, `"
                    + COLUMN_IS_REMOTE + "` integer not null default 0, `"
                    + COLUMN_GUESS + "` integer not null default 0, `"
                    + COLUMN_CORRECT_GUESS + "` integer not null default 0, `"
                    + COLUMN_IS_BLACKLISTED + "` integer not null default 0, "
                    + "PRIMARY KEY (`" + COLUMN_NAME + "`, `" + COLUMN_ARTIST + "`)"
                    + ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(int oldVersion, int newVersion, SQLiteDatabase db) {
        Log.w(TracksTable.class.getName(), String.format
                ("Upgrading table %1 from v.%2 to v.%3",
                        TABLE_NAME, oldVersion, newVersion));
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}

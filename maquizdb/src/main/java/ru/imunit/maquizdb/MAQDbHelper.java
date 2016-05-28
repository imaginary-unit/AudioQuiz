package ru.imunit.maquizdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.imunit.maquizdb.tables.BlackDirsTable;
import ru.imunit.maquizdb.tables.GamesTable;
import ru.imunit.maquizdb.tables.TracksTable;

/**
 * Created by imunit on 26.09.15.
 */
public class MAQDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "maquiz.db";

    public MAQDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        GamesTable.onCreate(db);
        TracksTable.onCreate(db);
        BlackDirsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MAQDbHelper.class.getName(), String.format("Upgrading database %1 from v.%2 to v.%3",
                DATABASE_NAME, oldVersion, newVersion));
        GamesTable.onUpgrade(oldVersion, newVersion, db);
        TracksTable.onUpgrade(oldVersion, newVersion, db);
        BlackDirsTable.onUpgrade(oldVersion, newVersion, db);
    }
}

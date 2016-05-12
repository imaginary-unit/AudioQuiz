package ru.imunit.maquizdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imunit on 29.09.2015.
 */
public class MAQDataSource {

    private SQLiteDatabase database;
    private MAQDbHelper dbHelper;

//    private static final String[] trackKeyCols =
//            { TracksTable.COLUMN_NAME, TracksTable.COLUMN_ARTIST };
    private static final String[] trackCols =
            { TracksTable.COLUMN_NAME, TracksTable.COLUMN_ARTIST, TracksTable.COLUMN_URI,
              TracksTable.COLUMN_IS_REMOTE, TracksTable.COLUMN_GUESS,
              TracksTable.COLUMN_CORRECT_GUESS };

    public MAQDataSource(Context context) {
        dbHelper = new MAQDbHelper(context);
    }

    public void openReadable() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void openWritable() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    // Database operations


    public DBTrack addOrFindTrack(String name, String artist, String uri, boolean isRemote)
    {
        // check if entry already exists
        String[] selArgs = { name, artist };
        Cursor cur = database.query(TracksTable.TABLE_NAME, trackCols,
                TracksTable.COLUMN_NAME + "=? AND " + TracksTable.COLUMN_ARTIST + "=?",
                selArgs, null, null, null);

        // if not - create it
        if (cur.getCount() == 0) {
            ContentValues cvals = new ContentValues();
            cvals.put(TracksTable.COLUMN_NAME, name);
            cvals.put(TracksTable.COLUMN_ARTIST, artist);
            cvals.put(TracksTable.COLUMN_URI, uri);
            cvals.put(TracksTable.COLUMN_IS_REMOTE, isRemote);

            database.insert(TracksTable.TABLE_NAME, null, cvals);
            cur = database.query(TracksTable.TABLE_NAME, trackCols,
                    TracksTable.COLUMN_NAME + "=? AND " + TracksTable.COLUMN_ARTIST + "=?",
                    selArgs, null, null, null);
        }

        // map to object and return
        cur.moveToFirst();
        DBTrack track = cursorToTrack(cur);
        cur.close();
        return track;
    }

    public DBTrack addOrFindTrack(String name, String artist) {
        return addOrFindTrack(name, artist, "", false);
    }

    public DBTrack getTrack(String name, String artist) {
        String[] selArgs = { name, artist };
        Cursor cur = database.query(TracksTable.TABLE_NAME, trackCols,
                TracksTable.COLUMN_NAME + "=? AND " + TracksTable.COLUMN_ARTIST + "=?",
                selArgs, null, null, null);
        if (cur.getCount() != 0) {
            cur.moveToFirst();
            DBTrack track = cursorToTrack(cur);
            cur.close();
            return track;
        }
        else {
            cur.close();
            return null;
        }
    }

    public String[] getDirectories() {
        Cursor cur = database.query(DirectoriesTable.TABLE_NAME,
                null, null, null, null, null, null);
        int cnt = cur.getCount();
        String[] res = new String[cnt];
        cur.moveToFirst();
        for (int i=0; i < cnt; i++) {
            res[i] = cur.getString(0);
            cur.moveToNext();
        }
        return res;
    }

    private DBTrack cursorToTrack(Cursor cur) {
        DBTrack track = new DBTrack();
        track.setName(cur.getString(0));
        track.setArtist(cur.getString(1));
        track.setUri(cur.getString(4));
        track.setIsRemote(cur.getShort(5));
        track.setGuess(cur.getLong(6));
        track.setCorrectGuess(cur.getLong(7));
        return track;
    }
}

package ru.imunit.maquizdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import ru.imunit.maquizdb.entities.DBGame;
import ru.imunit.maquizdb.entities.DBTrack;
import ru.imunit.maquizdb.tables.BlackDirsTable;
import ru.imunit.maquizdb.tables.GamesTable;
import ru.imunit.maquizdb.tables.TracksTable;

/**
 * Created by imunit on 29.09.2015.
 */
public class MAQDataSource implements IDataSource {

    private SQLiteDatabase database;
    private MAQDbHelper dbHelper;

//    private static final String[] trackKeyCols =
//            { TracksTable.COLUMN_NAME, TracksTable.COLUMN_ARTIST };
    private static final String[] trackCols =
            { TracksTable.COLUMN_NAME, TracksTable.COLUMN_ARTIST, TracksTable.COLUMN_URI,
              TracksTable.COLUMN_IS_REMOTE, TracksTable.COLUMN_GUESS,
              TracksTable.COLUMN_CORRECT_GUESS, TracksTable.COLUMN_IS_BLACKLISTED };

    public MAQDataSource(Context context) {
        dbHelper = new MAQDbHelper(context);
    }

    public void foo() {
        database = dbHelper.getReadableDatabase();
    }

    public boolean openReadable() {
        try {
            database = dbHelper.getReadableDatabase();
        }
        catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    public boolean openWritable() {
        try {
            database = dbHelper.getWritableDatabase();
        }
        catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    public void close() {
        dbHelper.close();
    }


    // Database operations

    public void addTracks(DBTrack[] tracks) {
        database.beginTransaction();
        for (DBTrack track : tracks) {
            ContentValues cvals = new ContentValues();
            cvals.put(TracksTable.COLUMN_NAME, track.getName());
            cvals.put(TracksTable.COLUMN_ARTIST, track.getArtist());
            cvals.put(TracksTable.COLUMN_URI, track.getUri());
            database.insert(TracksTable.TABLE_NAME, null, cvals);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public DBTrack addOrFindTrack(String name, String artist, String uri) {
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
        return addOrFindTrack(name, artist, "");
    }

    public DBTrack[] getAllTracks() {
        Cursor cur = database.query(TracksTable.TABLE_NAME, trackCols,
                null, null, null, null, null);
        if (cur != null) {
            int n = cur.getCount();
            DBTrack[] result = new DBTrack[n];
            if (n != 0) {
                cur.moveToFirst();
                int i = 0;
                do {
                    result[i] = cursorToTrack(cur);
                    i++;
                } while (cur.moveToNext());
            }
            cur.close();
            return result;
        } else {
            return new DBTrack[0];
        }
    }

    @Override
    public DBTrack[] getRandomTracks(int count) {
        String sql = String.format(Locale.ENGLISH, "select * from `%s` order by random() limit %d",
                TracksTable.TABLE_NAME, count);
        Cursor cur = database.rawQuery(sql, null);
        if (cur != null) {
            int n = cur.getCount();
            DBTrack[] result = new DBTrack[n];
            if (n != 0) {
                cur.moveToFirst();
                int i = 0;
                do {
                    result[i] = cursorToTrack(cur);
                    i++;
                } while (cur.moveToNext());
            }
            cur.close();
            return result;
        } else {
            return new DBTrack[0];
        }
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

    public String[] getBlackDirs() {
        Cursor cur = database.query(BlackDirsTable.TABLE_NAME,
                null, null, null, null, null, null);
        int cnt = cur.getCount();
        String[] res = new String[cnt];
        cur.moveToFirst();
        for (int i=0; i < cnt; i++) {
            res[i] = cur.getString(0);
            cur.moveToNext();
        }
        cur.close();
        return res;
    }

    public void deleteTracks(DBTrack[] tracks) {
        String where = TracksTable.COLUMN_NAME + "=? AND " + TracksTable.COLUMN_ARTIST + "=?";
        database.beginTransaction();
        for (DBTrack track : tracks) {
            String[] whereArgs = { track.getName(), track.getArtist() };
            database.delete(TracksTable.TABLE_NAME, where, whereArgs);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @Override
    public void addGame(DBGame game) {
        ContentValues cvals = new ContentValues();
        cvals.put(GamesTable.COLUMN_SCORE, game.getScore());
        cvals.put(GamesTable.COLUMN_GUESS, game.getGuess());
        cvals.put(GamesTable.COLUMN_CORRECT_GUESS, game.getCorrectGuess());
        cvals.put(GamesTable.COLUMN_AVG_GUESS_TIME, game.getAvgGuessTime());
        cvals.put(GamesTable.COLUMN_BEST_GUESS_TIME, game.getBestGuessTime());
        database.insert(GamesTable.TABLE_NAME, null, cvals);
    }

    @Override
    public void updateTracksGuesses(DBTrack[] tracks, int[] addGuesses, int[] addCorrectGuesses) {
        int n = tracks.length;

        List<DBTrack> tracksFull = new ArrayList<>();
        String selection = String.format("`%s` =? and `%s`=?",
                TracksTable.COLUMN_ARTIST, TracksTable.COLUMN_NAME);
        for (DBTrack track : tracks) {
            Cursor cur = database.query(TracksTable.TABLE_NAME, trackCols, selection,
                    new String[]{track.getArtist(), track.getName()}, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                tracksFull.add(cursorToTrack(cur));
            }
        }

        database.beginTransaction();
        for (int i=0; i < n; i ++) {
            long g = tracksFull.get(i).getGuess() + addGuesses[i];
            long cg = tracksFull.get(i).getCorrectGuess() + addCorrectGuesses[i];
            ContentValues cvals = new ContentValues();
            cvals.put(TracksTable.COLUMN_GUESS, g);
            cvals.put(TracksTable.COLUMN_CORRECT_GUESS, cg);
            database.update(TracksTable.TABLE_NAME, cvals, selection,
                    new String[]{tracksFull.get(i).getArtist(), tracksFull.get(i).getName()});
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private DBTrack cursorToTrack(Cursor cur) {
        DBTrack track = new DBTrack();
        track.setName(cur.getString(0));
        track.setArtist(cur.getString(1));
        track.setUri(cur.getString(2));
        track.setIsRemote(cur.getShort(3));
        track.setGuess(cur.getLong(4));
        track.setCorrectGuess(cur.getLong(5));
        track.setIsBlacklisted(cur.getShort(6));
        return track;
    }
}

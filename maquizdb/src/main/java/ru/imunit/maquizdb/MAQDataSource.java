package ru.imunit.maquizdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    @Override
    public boolean openReadable() {
//        try {
//            database = dbHelper.getReadableDatabase();
//        }
//        catch (SQLiteException e) {
//            return false;
//        }
//        return true;
        return false;
    }

    @Override
    public boolean openWritable() {
//        try {
//            database = dbHelper.getWritableDatabase();
//        }
//        catch (SQLiteException e) {
//            return false;
//        }
//        return true;
        return false;
    }

    public void close() {
        dbHelper.close();
    }


    // Database operations
    @Override
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

    @Override
    public void addDirToBlackList(String dir) {
        ContentValues cvals = new ContentValues();
        cvals.put(BlackDirsTable.COLUMN_PATH, dir);
        database.insert(BlackDirsTable.TABLE_NAME, null, cvals);
    }

    @Override
    public void removeDirFromBlackList(String dir) {
        database.delete(BlackDirsTable.TABLE_NAME, BlackDirsTable.COLUMN_PATH + "=?",
                new String[] {dir});
    }

    public void setTrackBlackListed(DBTrack track, boolean newState) {
        ContentValues cv = new ContentValues();
        cv.put(TracksTable.COLUMN_IS_BLACKLISTED, newState);
        String selection = String.format("`%s` =? and `%s`=?",
                TracksTable.COLUMN_ARTIST, TracksTable.COLUMN_NAME);
        database.update(TracksTable.TABLE_NAME, cv, selection,
                new String[] {track.getArtist(), track.getName()});
    }

    @Override
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
    public DBTrack[] getPlaybackTracks() {
        String selection = "`" + TracksTable.COLUMN_IS_BLACKLISTED + "` = 0";
        Cursor cur = database.query(TracksTable.TABLE_NAME, trackCols,
                selection, null, null, null, null);
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
    public DBTrack[] getGuessedTracks() {
        // select `artist`,`name`,`guess`,`correct_guess`,(`correct_guess`*1.0/`guess`) as r from `tracks` where `is_blacklisted` = 0 and `guess` > 0 order by r desc, `guess` desc;
        String sql = String.format(Locale.ENGLISH,
                "select *,(`%s`*1.0/`%s`) as r from `%s` where `%s` = 0 and `%s` > 0 order by r desc, `%s` desc",
                TracksTable.COLUMN_CORRECT_GUESS, TracksTable.COLUMN_GUESS, TracksTable.TABLE_NAME,
                TracksTable.COLUMN_IS_BLACKLISTED, TracksTable.COLUMN_GUESS, TracksTable.COLUMN_GUESS);
//        String selection = String.format(Locale.ENGLISH, "`%s` = 0 AND `%s` > 0",
//                TracksTable.COLUMN_IS_BLACKLISTED, TracksTable.COLUMN_GUESS);
//        Cursor cur = database.query(TracksTable.TABLE_NAME, trackCols,
//                selection, null, null, null, null);
        Cursor cur = database.rawQuery(sql, null);
        if (cur != null) {
            int n = cur.getCount();
            DBTrack[] result = new DBTrack[n];
            if (n != 0) {
                cur.moveToFirst();
                int i = 0;
                do {
                    DBTrack t = cursorToTrack(cur);
                    // float r = cur.getFloat(7);
                    result[i] = t;
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
        String sql = String.format(Locale.ENGLISH,
                "select * from `%s` where `%s` = 0 order by random() limit %d",
                TracksTable.TABLE_NAME, TracksTable.COLUMN_IS_BLACKLISTED, count);
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

    public DBTrack getTrack(String artist, String name) {
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

    @Override
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

    /*
      Statistics Data
     */

    @Override
    public Integer[] getTopScores(int count) {
        String sql = String.format(Locale.ENGLISH,
                "select `%s` from `%s` order by `%s` desc limit %d", GamesTable.COLUMN_SCORE,
                GamesTable.TABLE_NAME, GamesTable.COLUMN_SCORE, count);
        Cursor cur = database.rawQuery(sql, null);
        if (cur != null) {
            int n = cur.getCount();
            Integer[] scores = new Integer[n];
            if (n != 0) {
                cur.moveToFirst();
                int i = 0;
                do {
                    scores[i] = cur.getInt(0);
                    i++;
                } while (cur.moveToNext());
            }
            cur.close();
            return scores;
        } else {
            return new Integer[0];
        }
    }

    @Override
    public int getGamesCount() {
        String sql = "select count(*) from `" + GamesTable.TABLE_NAME + "`";
        Cursor cur = database.rawQuery(sql, null);
        if (cur != null) {
            cur.moveToFirst();
            int n = cur.getInt(0);
            cur.close();
            return n;
        } else {
            return 0;
        }
    }

    @Override
    public int getCleanGamesCount() {
        String sql = String.format(Locale.ENGLISH, "select count(*) from `%s` where `%s`=`%s`",
                GamesTable.TABLE_NAME, GamesTable.COLUMN_GUESS, GamesTable.COLUMN_CORRECT_GUESS);
        Cursor cur = database.rawQuery(sql, null);
        if (cur != null) {
            cur.moveToFirst();
            int n = cur.getInt(0);
            cur.close();
            return n;
        } else {
            return 0;
        }
    }

    @Override
    public float getCorrectGuessRatio() {
        // select sum(`correct_guess`), sum(`guess`) from `games`
        String sql = String.format(Locale.ENGLISH, "select sum(`%s`), sum(`%s`) from `%s`",
                GamesTable.COLUMN_CORRECT_GUESS, GamesTable.COLUMN_GUESS, GamesTable.TABLE_NAME);
        Cursor cur = database.rawQuery(sql, null);
        if (cur != null) {
            cur.moveToFirst();
            int cn = cur.getInt(0);
            int n = cur.getInt(1);
            cur.close();
            return (float)cn / (float)n;
        } else {
            return 0;
        }
    }

    @Override
    public int getAverageScore() {
        // select avg(`score`) from `games`
        String sql = String.format(Locale.ENGLISH, "select avg(`%s`) from `%s`",
                GamesTable.COLUMN_SCORE, GamesTable.TABLE_NAME);
        Cursor cur = database.rawQuery(sql, null);
        if (cur != null) {
            cur.moveToFirst();
            int score = Math.round(cur.getFloat(0));
            cur.close();
            return score;
        } else {
            return 0;
        }
    }

    @Override
    public int getLongestFastGuessRow() {
        // select max(`longest_...`) from `games`
        String sql = String.format(Locale.ENGLISH, "select max(`%s`) from `%s`",
                GamesTable.COLUMN_LONGEST_FAST_ROW, GamesTable.TABLE_NAME);
        Cursor cur = database.rawQuery(sql, null);
        if (cur != null) {
            cur.moveToFirst();
            int max = cur.getInt(0);
            cur.close();
            return max;
        } else {
            return 0;
        }
    }

    @Override
    public void clearStats() {
        ContentValues cvals = new ContentValues();
        cvals.put(TracksTable.COLUMN_GUESS, 0);
        cvals.put(TracksTable.COLUMN_CORRECT_GUESS, 0);
        database.beginTransaction();
        database.delete(GamesTable.TABLE_NAME, null, null);
        database.update(TracksTable.TABLE_NAME, cvals, null, null);
        database.setTransactionSuccessful();
        database.endTransaction();
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
        cvals.put(GamesTable.COLUMN_LONGEST_FAST_ROW, game.getLongestFastRow());
        database.insert(GamesTable.TABLE_NAME, null, cvals);
    }

    @Override
    public void updateTracksGuesses(DBTrack[] tracks, Integer[] addGuesses, Integer[] addCorrectGuesses) {
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

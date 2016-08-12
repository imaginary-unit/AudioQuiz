package ru.imunit.maquiz.managers;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ru.imunit.maquiz.R;
import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 19.05.16.
 */
public class MusicUpdater {

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = 1;
    public static final int RESULT_FEW_MUSIC = 2;
    public static final int RESULT_NO_MUSIC = 3;
    public static final int FEW_MUSIC_THRESHOLD = 20;

    private Context mContext;
    private MusicUpdateListener mListener;
    private List<String> mBlacklistedDirs;

    private class UpdateTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            return doUpdate();
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);
            mListener.onUpdateCompleted(res);
        }
    }

    public MusicUpdater(Context context) {
        mContext = context;
        if (mContext instanceof MusicUpdateListener)
            mListener = (MusicUpdateListener)mContext;
        mBlacklistedDirs = new ArrayList<>();
    }

    public void startUpdate() {
        new UpdateTask().execute();
    }

    public void setListener(MusicUpdateListener listener) {
        mListener = listener;
    }

    public int updateSync() {
        return doUpdate();
    }

    private int doUpdate() {
        // obtain app DB tracs
        IDataSource dataSource = DataSourceFactory.getDataSource(mContext);

        if (!dataSource.openWritable()) {
            return RESULT_ERROR;
        }

        DBTrack[] appTracks = dataSource.getAllTracks();
        HashSet<DBTrack> appSet = new HashSet<>();
        Collections.addAll(appSet, appTracks);
        Log.i("Tracks:", String.format("%d found on app DB", appSet.size()));

        // fill blacklisted directories
        mBlacklistedDirs = Arrays.asList(dataSource.getBlackDirs());

        // obtain MediaStore tracks
        HashSet<DBTrack> sysSet = getSystemMusic();
        Log.i("Tracks:", String.format("%d found on MediaStore", sysSet.size()));

        // compare sets and make DB insertions / deletions
        HashSet<DBTrack> delSet = new HashSet<>(appSet);
        delSet.removeAll(sysSet);
        HashSet<DBTrack> addSet = new HashSet<>(sysSet);
        addSet.removeAll(appSet);

        dataSource.deleteTracks(delSet.toArray(new DBTrack[delSet.size()]));
        Log.i("Tracks:", String.format("Deleting %d tracks from app DB", delSet.size()));
        dataSource.addTracks(addSet.toArray(new DBTrack[addSet.size()]));
        Log.i("Tracks:", String.format("Inserting %d tracks into app DB", addSet.size()));
        dataSource.close();

        int count = sysSet.size();
        if (count >= FEW_MUSIC_THRESHOLD) {
            return RESULT_OK;
        }
        else if (count == 0) {
            return RESULT_NO_MUSIC;
        }
        else {
            return RESULT_FEW_MUSIC;
        }
    }

    private HashSet<DBTrack> getSystemMusic() {
        //String[] STAR = { "*" };
        String[] projection = { MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA };
        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cur = mContext.getContentResolver().query(allSongsUri, projection, selection, null, null);
        HashSet<DBTrack> songs = new HashSet<>();
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    if (title == null)
                        title = mContext.getResources().getString(R.string.empty_tag_placeholder);
                    if (artist == null)
                        artist = mContext.getResources().getString(R.string.empty_tag_placeholder);
                    File f = new File(path);
                    if (f.exists()) {
                        DBTrack t = new DBTrack(title, artist, path);
                        if (checkTrackDir(t.getUri()))
                            songs.add(t);
                    }
                } while (cur.moveToNext());
            }
            cur.close();
        }
        return songs;
    }

    // function to check if a track at the given path is in an allowed directory
    private boolean checkTrackDir(String path) {
        File f = new File(path);
        String dir = f.getParent();
        return dir == null || !(mBlacklistedDirs.contains(dir));
    }

    public interface MusicUpdateListener {
        void onUpdateCompleted(int result);
    }

}

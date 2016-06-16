package ru.imunit.maquiz.managers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;

import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 19.05.16.
 */
public class MusicUpdater {

    private Context mContext;
    private MusicUpdateListener mListener;

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = 1;

    private class UpdateTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            return doUpdate();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mListener.onUpdateCompleted();
        }
    }

    public MusicUpdater(Context context) {
        mContext = context;
        if (mContext instanceof MusicUpdateListener)
            mListener = (MusicUpdateListener)mContext;
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

        return RESULT_OK;
    }

    private HashSet<DBTrack> getSystemMusic() {
        String[] STAR = { "*" };
        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cur = mContext.getContentResolver().query(allSongsUri, STAR, selection, null, null);
        HashSet<DBTrack> songs = new HashSet<>();
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    DBTrack t = new DBTrack(
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA))
                    );
                    songs.add(t);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        return songs;
    }

    public interface MusicUpdateListener {
        void onUpdateCompleted();
    }

}

package ru.imunit.maquiz.managers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;

import ru.imunit.maquizdb.entities.DBTrack;
import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.IDataSource;

/**
 * Created by lemoist on 19.05.16.
 */
public class MusicUpdater {

    Context mContext;

//    class UpdateTask extends AsyncTask<Void, Integer, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            return doUpdate();
//        }
//    }

    public MusicUpdater(Context context) {
        mContext = context;
    }

//    public void startUpdate() {
//
//    }

    public boolean updateSync() {
        return doUpdate();
    }

    private boolean doUpdate() {
        // obtain app DB tracs
        IDataSource dataSource = DataSourceFactory.getDataSource(mContext);

        if (!dataSource.openWritable()) {
            return false;
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
        return true;
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
}

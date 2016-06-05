package ru.imunit.maquiz.managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;

import ru.imunit.maquizdb.entities.DBTrack;
import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.IDataSource;

/**
 * Created by lemoist on 19.05.16.
 */
public class MusicUpdater implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;

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

        if (!checkStoragePermission())
            return false;

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

    private boolean checkStoragePermission() {
        boolean ok = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!ok) {
            if (mContext instanceof Activity) {
                Activity a = (Activity)mContext;
                ActivityCompat.requestPermissions(a,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_STORAGE);
            }
        }
        return ok;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }
}

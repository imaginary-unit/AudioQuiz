package ru.imunit.maquiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lemoist on 19.05.16.
 */
public class MusicUpdater {

    Context mContext;

    class UpdateTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            return true;
        }
    }

    public MusicUpdater(Context context) {
        mContext = context;
    }

    public void startUpdate() {

    }

    private void getAllMusic() {
        String[] STAR = { "*" };
        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cur = mContext.getContentResolver().query(allSongsUri, STAR, selection, null, null);
        if (cur != null) {
            ArrayList<String> songs = new ArrayList<String>();
            if (cur.moveToFirst()) {
                do {
                    String s = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    songs.add(s);
                    Log.i("Song found:", s);
                } while (cur.moveToNext());
            }
            cur.close();
        }
    }
}

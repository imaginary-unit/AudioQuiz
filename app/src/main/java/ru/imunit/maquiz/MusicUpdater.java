package ru.imunit.maquiz;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;

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

}

package ru.imunit.maquiz.models;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBTrack;


public class StatsModel implements IStatsModel {
    // model parameters
    private final int TOP_SCORES_COUNT = 5;
    private final int ACTION_PULL = 1;
    private final int ACTION_CLEAR = 0;
    // service model fields
    private IDataSource mDataSource;
    private List<ModelUpdateListener> mListeners;
    private AsyncExceptionListener mAEListener;
    // stats data
    private List<DBTrack> mTrackList;
    private List<Integer> mTopScores;
    private int mGamesCount;
    private int mCleanGamesCount;
    private float mCorrectGuessRatio;
    private int mAverageScore;
    private int mLongestFastGuessRow;


    public interface ModelUpdateListener {
        void onUpdateStarted();
        void onUpdateCompleted();
    }

    public interface AsyncExceptionListener {
        void onDatabaseException();
    }

    public void subscribe(ModelUpdateListener listener) {
        if (!mListeners.contains(listener))
            mListeners.add(listener);
    }

    public void unsubscribe(ModelUpdateListener listener) {
        mListeners.remove(listener);
    }

    public void setAEListener(AsyncExceptionListener aeListener) {
        mAEListener = aeListener;
    }


    public StatsModel(IDataSource dataSource) {
        mDataSource = dataSource;
        mListeners = new ArrayList<>();
        init();
    }

    public void startUpdate() {
        for (ModelUpdateListener listener : mListeners)
            listener.onUpdateStarted();
        new UpdateTask().execute(ACTION_PULL);
    }

    public void startClear() {
        for (ModelUpdateListener listener : mListeners)
            listener.onUpdateStarted();
        new UpdateTask().execute(ACTION_CLEAR);
    }

    @Override
    public List<DBTrack> getTracks() {
        return mTrackList;
    }

    @Override
    public List<Integer> getTopScores() {
        return mTopScores;
    }

    @Override
    public int getGamesCount() {
        return mGamesCount;
    }

    @Override
    public int getCleanGamesCount() {
        return mCleanGamesCount;
    }

    @Override
    public float getCorrectGuessRatio() {
        return mCorrectGuessRatio;
    }

    @Override
    public int getAverageScore() {
        return mAverageScore;
    }

    @Override
    public int getLongestFastGuessRow() {
        return mLongestFastGuessRow;
    }

    private class UpdateTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            if (params[0] == ACTION_PULL)
                return doUpdate();
            else if (params[0] == ACTION_CLEAR) {
                boolean r = doClear();
                if (r)
                    init();
                return r;
            }
            else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            for (ModelUpdateListener listener : mListeners)
                listener.onUpdateCompleted();
        }
    }

    private void init() {
        mTrackList = new ArrayList<>();
        mTopScores = new ArrayList<>();
        mGamesCount = 0;
        mCleanGamesCount = 0;
        mCorrectGuessRatio = 0;
        mAverageScore = 0;
        mLongestFastGuessRow = 0;
    }

    private boolean doClear() {
        if (!mDataSource.openWritable()) {
            if (mAEListener != null)
                mAEListener.onDatabaseException();
            return false;
        }
        mDataSource.clearStats();
        mDataSource.close();
        return true;
    }

    private boolean doUpdate() {
        if (!mDataSource.openReadable()) {
            if (mAEListener != null)
                mAEListener.onDatabaseException();
            return false;
        }
        mTrackList = Arrays.asList(mDataSource.getGuessedTracks());
        mTopScores = Arrays.asList(mDataSource.getTopScores(TOP_SCORES_COUNT));
        mGamesCount = mDataSource.getGamesCount();
        mCleanGamesCount = mDataSource.getCleanGamesCount();
        mCorrectGuessRatio = mDataSource.getCorrectGuessRatio();
        mAverageScore = mDataSource.getAverageScore();
        mLongestFastGuessRow = mDataSource.getLongestFastGuessRow();
        mDataSource.close();
        return true;
    }
}

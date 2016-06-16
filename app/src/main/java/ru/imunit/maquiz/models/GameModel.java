package ru.imunit.maquiz.models;

import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBGame;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 08.06.16.
 */

public class GameModel implements IGameModel {

    // playback won't start from a position later than this
    private static final float PLAYBACK_START_THRESHOLD = 0.75f;

    private IDataSource mDataSource;
    private List<ModelUpdateListener> mListeners;
    private List<DBTrack> mTracks;
    private HashMap<DBTrack, Integer> mGuess;
    private HashMap<DBTrack, Integer> mCorrectGuess;
    private List<Integer> mGuessTime;
    private DBTrack mCorrectTrack;
    private int mCurrentRound;
    private int mRoundsCount;
    private int mPlaybackTime;
    private float mPlaybackStartPos;
    private long mRoundScore;
    private long mGameScore;
    private int mOptionsCount;


    public GameModel(IDataSource dataSrc) {
        mDataSource = dataSrc;
        mListeners = new ArrayList<>();
        mGuess = new HashMap<>();
        mCorrectGuess = new HashMap<>();
        mGuessTime = new ArrayList<>();
    }

    public void subscribe(ModelUpdateListener listener) {
        mListeners.add(listener);
    }

    public void unsubscribe(ModelUpdateListener listener) {
        mListeners.remove(listener);
    }

    private Handler timerHandler = new Handler();
    private long mLastTime;
    private Runnable timerUpdate = new Runnable() {
        @Override
        public void run() {
            mPlaybackTime += (int)(System.currentTimeMillis() - mLastTime);
            mLastTime = System.currentTimeMillis();
            for (ModelUpdateListener listener : mListeners) {
                listener.onTimerUpdated(mPlaybackTime);
            }
            timerHandler.postDelayed(this, 10);
        }
    };

    private class ResultsWriter extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return writeStats();
        }
    }

    // Game logic

    private void finishGame() {
        new ResultsWriter().execute();
        for (ModelUpdateListener listener : mListeners) {
            listener.onGameFinished();
        }
    }

    private void addGuess(DBTrack track, boolean correct) {
        HashMap<DBTrack, Integer> hmap;
        if (correct)
            hmap = mCorrectGuess;
        else
            hmap = mGuess;
        int old = 0;
        if (hmap.containsKey(track))
            old = hmap.get(track);
        hmap.put(track, old + 1);
    }

    private boolean writeStats() {
        DBGame game = new DBGame();
        game.setScore(mGameScore);
        Integer s = 0;
        for (Integer val : mGuess.values())
            s += val;
        game.setGuess(s);
        s = 0;
        for (Integer val : mCorrectGuess.values())
            s += val;
        game.setCorrectGuess(s);
        long gtAvg = 0;
        for (Integer val : mGuessTime)
            gtAvg += val;
        gtAvg /= mGuessTime.size();
        game.setAvgGuessTime(gtAvg);
        game.setBestGuessTime(Collections.min(mGuessTime));

        if (!mDataSource.openWritable())
            return false;

        mDataSource.addGame(game);

        List<DBTrack> tracks = new ArrayList<>();
        List<Integer> addGuess = new ArrayList<>();
        List<Integer> addCorrectGuess = new ArrayList<>();

        for (DBTrack track : mGuess.keySet()) {
            tracks.add(track);
            addGuess.add(mGuess.get(track));
            if (mCorrectGuess.containsKey(track))
                addCorrectGuess.add(mCorrectGuess.get(track));
            else
                addCorrectGuess.add(0);
        }

        mDataSource.updateTracksGuesses(tracks.toArray(new DBTrack[0]),
                addGuess.toArray(new Integer[0]), addCorrectGuess.toArray(new Integer[0]));
        mDataSource.close();

        return true;
    }

    public void initGame(int options, int rounds) {
        mTracks = new ArrayList<>();
        mGuess.clear();
        mCorrectGuess.clear();
        mGuessTime.clear();
        mOptionsCount = options;
        mRoundsCount = rounds;
        mCurrentRound = 0;
        mPlaybackTime = 0;
        mPlaybackStartPos = 0f;
        mRoundScore = 0;
        mGameScore = 0;
    }

    public void nextRound() {
        mCurrentRound++;
        if (mCurrentRound > mRoundsCount) {
            finishGame();
            return;
        }
        // obtain random tracks from data source
        // TODO: handle possible exception
        mDataSource.openReadable();
        mTracks = Arrays.asList(mDataSource.getRandomTracks(mOptionsCount));
        mDataSource.close();
        // in case we don't have enough tracks in the playlist - take as many as possible
        int n = Math.min(mTracks.size(), mOptionsCount);
        mCorrectTrack = mTracks.get(new Random().nextInt(n));
        mPlaybackTime = 0;
        mPlaybackStartPos = 0f;
        mRoundScore = 10; // just for testing purposes
        // notify all listeners
        for (ModelUpdateListener listener : mListeners) {
            listener.onRoundUpdated();
        }
        startPlayback();
    }

    public void startPlayback() {
        mPlaybackStartPos = (new Random().nextFloat()) * PLAYBACK_START_THRESHOLD;
        for (ModelUpdateListener listener : mListeners) {
            listener.onPlaybackStarted();
        }
    }

    public void startTimer() {
        mLastTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerUpdate, 0);
    }

    public void makeGuess(DBTrack track) {
        addGuess(track, false);
        boolean result = track.equals(mCorrectTrack);
        if (result) {
            timerHandler.removeCallbacks(timerUpdate);
            mGuessTime.add(mPlaybackTime);
            addGuess(track, true);
            mGameScore += mRoundScore;
            for (ModelUpdateListener listener : mListeners) {
                listener.onScoreUpdated(mRoundScore);
            }
        }
        for (ModelUpdateListener listener : mListeners) {
            listener.onGuessVerified(result);
        }
    }

    // Public interface

    @Override
    public boolean isGameRunning() {
        return mCurrentRound > 0 && mCurrentRound <= mRoundsCount;
    }

    @Override
    public boolean isGameFinished() {
        return mCurrentRound > mRoundsCount;
    }


    @Override
    public int getCurrentRound() {
        return mCurrentRound;
    }

    @Override
    public int getRoundsCount() {
        return mRoundsCount;
    }

    @Override
    public int getPlaybackTime() {
        return mPlaybackTime;
    }

    @Override
    public float getPlaybackStartPos() {
        return mPlaybackStartPos;
    }

    @Override
    public long getGameScore() {
        return mGameScore;
    }

    @Override
    public long getRoundScore() {
        return mRoundScore;
    }

    @Override
    public List<DBTrack> getTracks() {
        return mTracks;
    }

    @Override
    public DBTrack getCorrectTrack() {
        return mCorrectTrack;
    }

    public interface ModelUpdateListener {
        void onRoundUpdated();
        void onScoreUpdated(long diff);
        void onTimerUpdated(int time);
        void onGuessVerified(boolean result);
        void onPlaybackStarted();
        void onGameFinished();
    }
}

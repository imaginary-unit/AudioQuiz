package ru.imunit.maquiz.models;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ru.imunit.maquizdb.IDataSource;
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
    private List<Long> mGuessTime;
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

    // Game logic

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
            for (ModelUpdateListener listener : mListeners) {
                listener.onGameFinished();
            }
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
        boolean result = track.equals(mCorrectTrack);
        if (result) {
            timerHandler.removeCallbacks(timerUpdate);
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

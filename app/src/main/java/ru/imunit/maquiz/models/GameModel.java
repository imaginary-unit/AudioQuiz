package ru.imunit.maquiz.models;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import ru.imunit.maquiz.exceptions.DatabaseException;
import ru.imunit.maquiz.exceptions.NoMusicException;
import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBGame;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 08.06.16.
 */

public class GameModel implements IGameModel {

    // playback won't start from a position later than this
    private static final float PLAYBACK_START_THRESHOLD = 0.75f;
    // base score for a correct guess
    private static final int SCORE_BASE = 10;
    // bonuses for guessing withing 1st, 2nd and 3rd time thresholds
    private static final int SCORE_BONUS_TIME_1 = 5;
    private static final int SCORE_BONUS_TIME_2 = 10;
    private static final int SCORE_BONUS_TIME_3 = 20;
    private static final int BONUS_TIME_THRESHOLD_1 = 10000;
    private static final int BONUS_TIME_THRESHOLD_2 = 5000;
    private static final int BONUS_TIME_THRESHOLD_3 = 3000;
    // number of 3rd-threshold guesses in a row required to start multiplying score by a factor
    private static final int ROW_BONUS_START = 3;
    private static final float ROW_BONUS_FACTOR = 2.0f;
    // factor to multiply the overall game score by in the case of no wrong guesses
    private static final float CLEAN_BONUS_FACTOR = 1.5f;
    // the number to divide the round score by for each wrong guess
    private static final int WRONG_GUESS_PENALTY = 2;

    public static final int GUESS_RESULT_CORRECT = 1;
    // the guess was incorrect, but there are other options left
    public static final int GUESS_RESULT_WRONG_CONTINUE = 0;
    // the guess was incorrect and there is the only option left
    public static final int GUESS_RESULT_WRONG_END = -1;


    // service model fields
    private IDataSource mDataSource;
    private List<ModelUpdateListener> mListeners;
    private AsyncExceptionListener mAEListener;
    // game properties
    private boolean mMetronomeEnabled;
    // game scope fields
    private HashMap<DBTrack, Integer> mGuess;
    private HashMap<DBTrack, Integer> mCorrectGuess;
    private List<Integer> mGuessTime;
    private int mRoundsCount;
    private long mGameScore;
    private long mLastHighscore;
    private int mOptionsCount;
    // round scope fields
    private int mCurrentRound;
    private List<DBTrack> mTracks;
    private HashSet<DBTrack> mTracksGuessed;
    private DBTrack mCorrectTrack;
    private int mPlaybackTime;
    private float mPlaybackStartPos;
    private long mRoundScore;
    private int mRoundPenalty;


    public GameModel(IDataSource dataSrc) {
        mDataSource = dataSrc;
        mListeners = new ArrayList<>();
        mGuess = new HashMap<>();
        mCorrectGuess = new HashMap<>();
        mGuessTime = new ArrayList<>();
        mMetronomeEnabled = true;
    }

    public void subscribe(ModelUpdateListener listener) {
        mListeners.add(listener);
    }

    public void unsubscribe(ModelUpdateListener listener) {
        mListeners.remove(listener);
    }

    public void setAsyncExceptionListener(AsyncExceptionListener aeListener) {
        mAEListener = aeListener;
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

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean && mAEListener != null) {
                mAEListener.onDatabaseException();
            }
        }
    }

    // Game logic

    private void finishGame() {
        // if the game was clean, multiply the score by a corresponding factor
        if (isGameClean())
            mGameScore *= CLEAN_BONUS_FACTOR;
        // obtain last highscore before writing this game
        if (!mDataSource.openReadable())
            throw new DatabaseException();
        Integer[] topScores = mDataSource.getTopScores(1);
        mDataSource.close();
        if (topScores.length > 0) {
            mLastHighscore = topScores[0];
        }
        // write game result to database
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
        Log.i("Game.score =", String.valueOf(game.getScore()));
        // number of guesses
        Integer s = 0;
        for (Integer val : mGuess.values())
            s += val;
        game.setGuess(s);
        Log.i("Game.guess =", String.valueOf(game.getGuess()));
        // number of correct guesses
        s = 0;
        for (Integer val : mCorrectGuess.values())
            s += val;
        game.setCorrectGuess(s);
        Log.i("Game.correct_guess =", String.valueOf(game.getCorrectGuess()));
        long gtAvg = 0;
        // calculating average and best guess time
        for (Integer val : mGuessTime)
            gtAvg += val;
        gtAvg /= mGuessTime.size();
        game.setAvgGuessTime(gtAvg);
        Log.i("Game.avg_guess_time =", String.valueOf(game.getAvgGuessTime()));
        game.setBestGuessTime(Collections.min(mGuessTime));
        Log.i("Game.best_guess_time =", String.valueOf(game.getBestGuessTime()));
        // finding the longest fast row
        int max = 0;
        int cur = 0;
        boolean fast = false;
        for (Integer val: mGuessTime) {
            fast = val <= BONUS_TIME_THRESHOLD_3;
            if (fast)
                cur++;
            else {
                if (cur > max) max = cur;
                cur = 0;
            }
        }
        game.setLongestFastRow(max);
        Log.i("Game.longest_fast_row =", String.valueOf(game.getLongestFastRow()));

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

    private void calcRoundScoreBonuses() {
        mRoundScore += SCORE_BASE;
        int n = mGuessTime.size();
        int t0 = mGuessTime.get(n-1); // current round time
        // adding time bonuses
        if (t0 <= BONUS_TIME_THRESHOLD_3) {
            mRoundScore += SCORE_BONUS_TIME_3;
            // checking for row bonus
            if (n >= ROW_BONUS_START) {
                int t1 = mGuessTime.get(n-2);
                int t2 = mGuessTime.get(n-3);
                if ((t1 <= BONUS_TIME_THRESHOLD_3) && (t2 <= BONUS_TIME_THRESHOLD_3))
                    mRoundScore = (int)(mRoundScore * ROW_BONUS_FACTOR);
            }
        }
        else if (t0 <= BONUS_TIME_THRESHOLD_2)
            mRoundScore += SCORE_BONUS_TIME_2;
        else if (t0 <= BONUS_TIME_THRESHOLD_1)
            mRoundScore += SCORE_BONUS_TIME_1;

        mRoundScore /= mRoundPenalty;
    }

    public void initGame(int options, int rounds) {
        mTracks = new ArrayList<>();
        mTracksGuessed = new HashSet<>();
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
        mLastHighscore = 0;
    }

    public void nextRound() throws DatabaseException, NoMusicException {
        if (!mDataSource.openReadable())
            throw new DatabaseException();

        mCurrentRound++;
        if (mCurrentRound > mRoundsCount) {
            mDataSource.close();
            finishGame();
            return;
        }
        mTracksGuessed.clear();
        // obtain random tracks from data source
        mTracks = Arrays.asList(mDataSource.getRandomTracks(mOptionsCount));
        mDataSource.close();
        // in case we don't have enough tracks in the playlist - take as many as possible
        int n = Math.min(mTracks.size(), mOptionsCount);
        // is there is no tracks at all - fire an exception
        if (n == 0) {
            throw new NoMusicException();
        }
        mCorrectTrack = mTracks.get(new Random().nextInt(n));
        mPlaybackTime = 0;
        mPlaybackStartPos = 0f;
        mRoundScore = 0;
        mRoundPenalty = 1;
        // notify all listeners
        for (ModelUpdateListener listener : mListeners) {
            listener.onRoundUpdated();
        }
        // startPlayback();
    }

    // TODO: handling track end (restart)
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
        int result = track.equals(mCorrectTrack) ?
                GUESS_RESULT_CORRECT : GUESS_RESULT_WRONG_CONTINUE;
        if (result == GUESS_RESULT_CORRECT) {
            timerHandler.removeCallbacks(timerUpdate);
            mGuessTime.add(mPlaybackTime);
            addGuess(track, true);
            calcRoundScoreBonuses();
            mGameScore += mRoundScore;
            for (ModelUpdateListener listener : mListeners) {
                listener.onScoreUpdated(mRoundScore);
            }
        } else {
            mTracksGuessed.add(track);
            // check if there is only one option left
            // if (mRoundPenalty >= Math.pow(WRONG_GUESS_PENALTY, mOptionsCount-2)) {
            if (mOptionsCount - mTracksGuessed.size() < 2) {
                timerHandler.removeCallbacks(timerUpdate);
                mRoundScore = 0;
                result = GUESS_RESULT_WRONG_END;
            } else {
                mRoundPenalty *= WRONG_GUESS_PENALTY;
            }
        }
        for (ModelUpdateListener listener : mListeners) {
            listener.onGuessVerified(result);
        }
    }

    public void setMetronomeEnabled(boolean state) {
        mMetronomeEnabled = state;
    }

    // Public interface

    @Override
    public boolean isMetronomeEnabled() {
        return mMetronomeEnabled;
    }

    @Override
    public boolean isPlaybackStarted() {
        return isGameRunning() && (mPlaybackTime > 0);
    }

    @Override
    public boolean isGameRunning() {
        return mCurrentRound > 0 && mCurrentRound <= mRoundsCount;
    }

    @Override
    public boolean isGameFinished() {
        return mCurrentRound > mRoundsCount;
    }

    @Override
    public boolean isGameClean() {
        Integer s = 0; // number of guesses
        for (Integer val : mGuess.values())
            s += val;
        Integer cs = 0; // number of correct guesses
        for (Integer val : mCorrectGuess.values())
            cs += val;
        return s == cs;
    }

    @Override
    public boolean isTrackGuessed(DBTrack track) {
        return mTracksGuessed.contains(track);
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
    public long getLastHighscore() {
        return mLastHighscore;
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
        void onGuessVerified(int result);
        void onPlaybackStarted();
        void onGameFinished();
    }

    public interface AsyncExceptionListener {
        void onDatabaseException();
    }
}

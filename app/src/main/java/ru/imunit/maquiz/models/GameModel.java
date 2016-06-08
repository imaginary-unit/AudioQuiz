package ru.imunit.maquiz.models;

import java.util.HashMap;
import java.util.List;

import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 08.06.16.
 */

public class GameModel {

    private List<DBTrack> mTracks;
    private HashMap<DBTrack, Integer> mGuess;
    private HashMap<DBTrack, Integer> mCorrectGuess;
    private List<Long> mGuessTime;
    private int mCurrentRound;
    private int mRoundsCount;
    private long mTimerData;
    private long mRoundScore;
    private long mGameScore;


    public interface ModelUpdateListener {
        void onRoundUpdated();
        void onScoreUpdated(long diff);
        void onTimerUpdated();
        void onGuessVerified(boolean result);
    }
}

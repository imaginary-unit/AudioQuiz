package ru.imunit.maquiz.models;

import java.util.List;

import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by theuser on 08.07.16.
 */

public interface IStatsModel {
    List<DBTrack> getTracks();
    List<Integer> getTopScores();
    int getGamesCount();
    int getCleanGamesCount();
    float getCorrectGuessRatio();
    int getAverageScore();
    int getLongestFastGuessRow();
    int getFastestGuessTime();
    int getAverageGuessTime();
}

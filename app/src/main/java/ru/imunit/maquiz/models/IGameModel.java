package ru.imunit.maquiz.models;

import java.util.List;

import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 08.06.16.
 */

public interface IGameModel {
    boolean isGameRunning();
    boolean isGameFinished();
    int getCurrentRound();
    int getRoundsCount();
    int getPlaybackTime();
    float getPlaybackStartPos();
    long getGameScore();
    long getRoundScore();
    List<DBTrack> getTracks();
    DBTrack getCorrectTrack();
}

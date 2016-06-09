package ru.imunit.maquiz.models;

import java.util.List;

import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 08.06.16.
 */

public interface IGameModel {
    int getCurrentRound();
    int getRoundsCount();
    long getTimerData();
    long getGameScore();
    long getRoundScore();
    List<DBTrack> getTracks();
    DBTrack getCorrectTrack();
}

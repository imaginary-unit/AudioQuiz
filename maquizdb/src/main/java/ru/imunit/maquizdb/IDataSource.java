package ru.imunit.maquizdb;

import java.util.Map;

import ru.imunit.maquizdb.entities.DBGame;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 27.05.16.
 */
public interface IDataSource {
    boolean openReadable();
    boolean openWritable();
    void close();
    void addTracks(DBTrack[] tracks);
    void deleteTracks(DBTrack[] tracks);
    void addGame(DBGame game);
    void updateTracksGuesses(DBTrack[] tracks, Integer[] addGuesses, Integer[] addCorrectGuesses);
    void addDirToBlackList(String dir);
    void removeDirFromBlackList(String dir);
    void setTrackBlackListed(DBTrack track, boolean newState);
    DBTrack[] getAllTracks();
    DBTrack[] getPlaybackTracks();  // returns all tracks that aren't in the black list
    DBTrack[] getGuessedTracks();   // returns all non-blacklisted tracks, that have been guessed at least once
    DBTrack[] getRandomTracks(int count);
    DBTrack getTrack(String artist, String name);
    String[] getBlackDirs();
    // statistics data
    Integer[] getTopScores(int count);
    int getGamesCount();
    int getCleanGamesCount();
    float getCorrectGuessRatio();
    int getAverageScore();
    int getLongestFastGuessRow();
    void clearStats();
}

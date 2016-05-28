package ru.imunit.maquizdb;

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
    DBTrack[] getAllTracks();
}

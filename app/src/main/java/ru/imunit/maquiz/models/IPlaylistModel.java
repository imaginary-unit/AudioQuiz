package ru.imunit.maquiz.models;

import java.util.HashMap;
import java.util.List;

import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by theuser on 27.06.16.
 */

public interface IPlaylistModel {
    List<DBTrack> getAllTracks();
    List<DBTrack> getBlackList();
    HashMap<String, Boolean> getDirectories();
    boolean getUpdateRequired();
}

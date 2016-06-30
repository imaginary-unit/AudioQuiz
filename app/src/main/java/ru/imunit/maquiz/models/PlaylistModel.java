package ru.imunit.maquiz.models;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ru.imunit.maquiz.managers.MusicUpdater;
import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by theuser on 27.06.16.
 */

public class PlaylistModel implements IPlaylistModel {

    public interface ModelUpdateListener {
        void onDataUpdated();
    }

    public PlaylistModel(IDataSource dataSource) {
        mDataSource = dataSource;
        mUpdateRequired = true;
        mListeners = new ArrayList<>();
    }

    public void subscribe(ModelUpdateListener listener) {
        mListeners.add(listener);
    }

    public void unsubscribe(ModelUpdateListener listener) {
        mListeners.remove(listener);
    }

    // Model manipulation methods

    public void initUpdate(Context context) {
        if (mUpdateRequired) {
            updateMusic(context);
        }
        else {
            for (ModelUpdateListener listener : mListeners)
                listener.onDataUpdated();
        }
    }

    public void setDirectoryState(String dir, boolean newState) {
        mDataSource.openWritable();
        if (newState)
            mDataSource.removeDirFromBlackList(dir);
        else
            mDataSource.addDirToBlackList(dir);
        mDataSource.close();
        // remember that data update is required..
        mUpdateRequired = true;
        // .. but set new state value manually to avoid pulling it from the DB on each request
        mDirectories.put(dir, newState);
        for (ModelUpdateListener listener : mListeners)
            listener.onDataUpdated();
    }

    // ----

    @Override
    public List<DBTrack> getAllTracks() {
        return mAllTracks;
    }

    @Override
    public List<DBTrack> getBlackList() {
        List<DBTrack> tracksBlackList = new ArrayList<>();
        for (DBTrack track : mAllTracks) {
            if (track.getIsBlacklisted() == 1) {
                tracksBlackList.add(track);
            }
        }
        return tracksBlackList;
    }

    @Override
    public HashMap<String, Boolean> getDirectories() {
        return mDirectories;
    }

    @Override
    public boolean getUpdateRequired() {
        return mUpdateRequired;
    }

    private void updateMusic(Context context) {
        MusicUpdater updater = new MusicUpdater(context);
        updater.setListener(new MusicUpdater.MusicUpdateListener() {
            @Override
            public void onUpdateCompleted() {
                // TODO: handle exception
                mDataSource.openReadable();
                mAllTracks = new ArrayList<>(Arrays.asList(mDataSource.getAllTracks()));
                List<String> bDirs = Arrays.asList(mDataSource.getBlackDirs());
                mDirectories = new HashMap<>();
                for (String s : bDirs)
                    mDirectories.put(s, false);
                for (DBTrack track : mAllTracks) {
                    String dir = new File(track.getUri()).getParent();
                    if (!mDirectories.containsKey(dir)) {
                        mDirectories.put(dir, true);
                    }
                }
                mDataSource.close();
                mUpdateRequired = false;
                for (ModelUpdateListener listener : mListeners)
                    listener.onDataUpdated();
            }
        });
        updater.startUpdate();
    }

    private IDataSource mDataSource;
    private List<ModelUpdateListener> mListeners;
    private List<DBTrack> mAllTracks;
    private HashMap<String, Boolean> mDirectories;
    private boolean mUpdateRequired;
}

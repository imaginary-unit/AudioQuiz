package ru.imunit.maquiz.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.managers.MusicUpdater;
import ru.imunit.maquiz.views.adapters.CheckRecyclerAdapter;
import ru.imunit.maquiz.views.adapters.PlaylistRecyclerAdapter;
import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistTracksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PlaylistTracksFragment extends Fragment implements
        CheckRecyclerAdapter.ItemClickListener {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecycler;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayout;
    private ProgressDialog mProgressDialog = null;
    private boolean mShowBlackList;

    public PlaylistTracksFragment() {
        mShowBlackList = false;
    }

    public void setShowBlackList(boolean state) {
        mShowBlackList = state;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_viewer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecycler = (RecyclerView)getView().findViewById(R.id.recycler);
        mRecyclerLayout = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mRecyclerLayout);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateMusic() {
        mProgressDialog = ProgressDialog.show(getContext(), "Please, wait",
                "Syncronizing the music database...", true, false);
        MusicUpdater updater = new MusicUpdater(getActivity());
        updater.setListener(new MusicUpdater.MusicUpdateListener() {
            @Override
            public void onUpdateCompleted() {
                if (PlaylistTracksFragment.this.mProgressDialog != null)
                    PlaylistTracksFragment.this.mProgressDialog.dismiss();
                IDataSource dataSource = DataSourceFactory.getDataSource(getActivity());
                // TODO: handle exception
                dataSource.openReadable();
                mTrackList = new ArrayList<>(Arrays.asList(dataSource.getAllTracks()));
                dataSource.close();
                if (mCurrentMode == ALL_TRACKS) {
                    Log.i("Update completed", "1");
                    onViewSwitchAllTracks();
                } else if (mCurrentMode == BLACK_LIST) {
                    Log.i("Update completed", "2");
                    onViewSwitchBlackList();
                } else {
                    Log.i("Update completed", "3");
                    onViewSwitchMusicDirectories();
                }
            }
        });
        updater.startUpdate();
    }

    private void onViewSwitchMusicDirectories() {
        IDataSource dataSource = DataSourceFactory.getDataSource(getActivity());
        // TODO: handle exception
        dataSource.openReadable();
        List<String> bDirs = Arrays.asList(dataSource.getBlackDirs());
        dataSource.close();

        mDirectories = new HashMap<>();
        for (String s : bDirs)
            mDirectories.put(s, false);

        for (DBTrack track : mTrackList) {
            String dir = new File(track.getUri()).getParent();
            if (!mDirectories.containsKey(dir)) {
                mDirectories.put(dir, true);
            }
        }

        mRecyclerAdapter = new CheckRecyclerAdapter(mDirectories);
        ((CheckRecyclerAdapter)mRecyclerAdapter).setOnClickListener(this);
        mRecycler.setAdapter(mRecyclerAdapter);
    }

    private void onViewSwitchBlackList() {
        List<DBTrack> tracksBlackList = new ArrayList<>();
        for (DBTrack track : mTrackList) {
            if (track.getIsBlacklisted() == 1) {
                tracksBlackList.add(track);
            }
        }
        mRecyclerAdapter = new PlaylistRecyclerAdapter(tracksBlackList);
        mRecycler.setAdapter(mRecyclerAdapter);
    }

    private void onViewSwitchAllTracks() {
        // mRecycler.setHasFixedSize(true);
        mRecyclerAdapter = new PlaylistRecyclerAdapter(mTrackList);
        mRecycler.setAdapter(mRecyclerAdapter);
    }

    @Override // Directory click handler
    public void onClick(String dir, boolean state) {
        boolean newState = !state;

        IDataSource dataSource = DataSourceFactory.getDataSource(getActivity());
        // TODO: handle exception
        dataSource.openWritable();
        if (newState)
            dataSource.removeDirFromBlackList(dir);
        else
            dataSource.addDirToBlackList(dir);
        dataSource.close();

        mDirectories.put(dir, newState);
        mRecyclerAdapter.notifyDataSetChanged();
        mUpdateRequired = true;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}

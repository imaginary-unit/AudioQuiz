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
import android.widget.Spinner;

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
 * {@link PlaylistViewerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PlaylistViewerFragment extends Fragment implements
        CheckRecyclerAdapter.ItemClickListener {

    private final int ALL_TRACKS = 0;
    private final int BLACK_LIST = 1;
    private final int MUSIC_DIRECTORIES = 2;

    private int mCurrentMode = ALL_TRACKS;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecycler;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayout;
    private List<DBTrack> mTrackList;
    private HashMap<String, Boolean> mDirectories;
    private ProgressDialog mProgressDialog = null;
    private AppCompatSpinner mViewModeSpinner;
    private boolean mUpdateRequired = false;

    public PlaylistViewerFragment() {
        // Required empty public constructor
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
        mTrackList = new ArrayList<>();
        mDirectories = new HashMap<>();
        initSpinner();
        mRecycler = (RecyclerView)getView().findViewById(R.id.recycler);
        mRecyclerLayout = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mRecyclerLayout);
        updateMusic();
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
                if (PlaylistViewerFragment.this.mProgressDialog != null)
                    PlaylistViewerFragment.this.mProgressDialog.dismiss();
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

    private boolean mSpinnerCalledOnce = false;
    private void initSpinner() {
        mViewModeSpinner = (AppCompatSpinner)getView().findViewById(R.id.view_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.playlist_view_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
        mViewModeSpinner.setAdapter(adapter);
        mViewModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // a dirty hack to workaround an unwanted onItemSelected call on initialization
                if (mSpinnerCalledOnce) {
                    if (mUpdateRequired) {
                        mUpdateRequired = false;
                        updateMusic();
                    } else {
                        if (position == ALL_TRACKS) {
                            Log.i("Spinner click", "1");
                            onViewSwitchAllTracks();
                        } else if (position == BLACK_LIST) {
                            Log.i("Spinner click", "2");
                            onViewSwitchBlackList();
                        } else {
                            Log.i("Spinner click", "3");
                            onViewSwitchMusicDirectories();
                        }
                    }
                } else {
                    Log.i("Dummy call!", "0");
                    mSpinnerCalledOnce = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

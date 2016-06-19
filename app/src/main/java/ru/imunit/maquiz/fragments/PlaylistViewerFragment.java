package ru.imunit.maquiz.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.managers.MusicUpdater;
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
public class PlaylistViewerFragment extends Fragment {

    private final int ALL_TRACKS = 0;
    private final int BLACK_LIST = 1;
    private final int MUSIC_DIRECTORIES = 2;

    private int mCurrentMode = ALL_TRACKS;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecycler;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayout;
    private List<DBTrack> mTrackList;
    private ProgressDialog mProgressDialog = null;
    private AppCompatSpinner mViewModeSpinner;

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
        initSpinner();
        mRecycler = (RecyclerView)getView().findViewById(R.id.recycler);
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

                if (mCurrentMode == ALL_TRACKS) {
                    onViewSwitchAllTracks();
                } else if (mCurrentMode == BLACK_LIST) {
                    onViewSwitchBlackList();
                } else {
                    onViewSwitchMusicDirectories();
                }
            }
        });
        updater.startUpdate();
    }

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
                if (position == ALL_TRACKS) {
                    onViewSwitchAllTracks();
                } else if (position == BLACK_LIST) {
                    onViewSwitchBlackList();
                } else {
                    onViewSwitchMusicDirectories();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onViewSwitchMusicDirectories() {

    }

    private void onViewSwitchBlackList() {

    }

    private void onViewSwitchAllTracks() {
        mRecycler.setHasFixedSize(true);
        mRecyclerLayout = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mRecyclerLayout);
        mRecyclerAdapter = new PlaylistRecyclerAdapter(mTrackList);
        mRecycler.setAdapter(mRecyclerAdapter);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}

package ru.imunit.maquiz.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecycler;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayout;
    private List<DBTrack> mTrackList;
    private ProgressDialog mProgressDialog = null;

    public PlaylistViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_viewer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateMusic();
        // initRecycler();
        FloatingActionButton fab = (FloatingActionButton)getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "bla-bla", Toast.LENGTH_SHORT).show();
            }
        });

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
                initRecycler();
            }
        });
        updater.startUpdate();
    }

    private void initRecycler() {
        IDataSource dataSource = DataSourceFactory.getDataSource(getActivity());
        // TODO: handle exception
        dataSource.openReadable();
        mTrackList = new ArrayList<>(Arrays.asList(dataSource.getAllTracks()));

        mRecycler = (RecyclerView)getView().findViewById(R.id.recycler);
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

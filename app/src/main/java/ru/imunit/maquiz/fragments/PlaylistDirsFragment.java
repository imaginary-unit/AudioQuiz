package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.models.IPlaylistModel;
import ru.imunit.maquiz.models.PlaylistModel;
import ru.imunit.maquiz.views.adapters.CheckRecyclerAdapter;


public class PlaylistDirsFragment extends Fragment implements
        PlaylistModel.ModelUpdateListener,
        CheckRecyclerAdapter.ItemClickListener {

    private OnFragmentInteractionListener mListener;
    private IPlaylistModel mModel;
    private RecyclerView mRecycler;
    private CheckRecyclerAdapter mAdapter;

    public PlaylistDirsFragment() {
        // Required empty public constructor
    }

    public void setModel(IPlaylistModel model) {
        mModel = model;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameStatsListener");
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
        mRecycler = (RecyclerView) getView().findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDataUpdated() {
        mAdapter = new CheckRecyclerAdapter(mModel.getDirectories());
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnClickListener(this);
    }

    @Override  // CheckRecyclerAdapter item click handler
    public void onClick(String dir, boolean state) {
        mListener.onDirectoryClick(dir, state);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment
     */
    public interface OnFragmentInteractionListener {
        void onDirectoryClick(String dir, boolean state);
    }
}

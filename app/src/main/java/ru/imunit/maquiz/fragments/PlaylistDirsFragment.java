package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.models.IPlaylistModel;
import ru.imunit.maquiz.models.PlaylistModel;
import ru.imunit.maquiz.views.adapters.CheckRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistDirsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
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
                    + " must implement OnFragmentInteractionListener");
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
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onDirectoryClick(String dir, boolean state);
    }
}

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
import ru.imunit.maquiz.models.IStatsModel;
import ru.imunit.maquiz.models.StatsModel;
import ru.imunit.maquiz.views.adapters.StatsRecyclerAdapter;


public class TrackStatsFragment extends Fragment implements
        StatsModel.ModelUpdateListener {

    private TrackStatsListener mListener;
    private IStatsModel mModel;

    public interface TrackStatsListener {
        void onTrackStatsInitialized();
    }

    public TrackStatsFragment() {
        // Required empty public constructor
    }

    public void setModel(IStatsModel model) {
        mModel = model;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track_stats, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TrackStatsListener) {
            mListener = (TrackStatsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameStatsListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mListener != null)
            mListener.onTrackStatsInitialized();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onUpdateStarted() {

    }

    @Override
    public void onUpdateCompleted() {
        RecyclerView rv = (RecyclerView)getView().findViewById(R.id.tracksRecycler);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        StatsRecyclerAdapter adapter = new StatsRecyclerAdapter(getContext(), mModel.getTracks());
        rv.setAdapter(adapter);
    }

}

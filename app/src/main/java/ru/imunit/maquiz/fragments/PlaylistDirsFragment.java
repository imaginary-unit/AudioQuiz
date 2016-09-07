package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.managers.SettingsManager;
import ru.imunit.maquiz.models.IPlaylistModel;
import ru.imunit.maquiz.models.PlaylistModel;
import ru.imunit.maquiz.models.StatsModel;
import ru.imunit.maquiz.views.adapters.CheckRecyclerAdapter;


public class PlaylistDirsFragment extends Fragment implements
        PlaylistModel.ModelUpdateListener,
        CheckRecyclerAdapter.ItemClickListener {

    private OnFragmentInteractionListener mListener;
    private IPlaylistModel mModel;
    private RecyclerView mRecycler;
    private CheckRecyclerAdapter mAdapter;
    private boolean mNotifyDisableDir;

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
        mNotifyDisableDir = new SettingsManager(getContext()).getNotifyDisableDir();
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
        if (state && mNotifyDisableDir)
            notifyDisableDir(dir);
        else
            mListener.onDirectoryClick(dir, state);
    }

    // 1 - yes, disable; 0 - no, cancel
    private void notifyDisableDir(final String dir) {
        View dialogView = View.inflate(getContext(), R.layout.dialog_check, null);
        TextView dialogText = (TextView)dialogView.findViewById(R.id.dialogText);
        dialogText.setText(R.string.playlist_dir_notification_message);
        CheckBox dialogCheck = (CheckBox)dialogView.findViewById(R.id.dialogCheck);
        dialogCheck.setText(R.string.playlist_dir_notification_dont_show);
        dialogCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox)view;
                if (cb.isChecked()) {
                    //cb.setChecked(true);
                    mNotifyDisableDir = false;
                }
                else {
                    // cb.setChecked(false);
                    mNotifyDisableDir = true;
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.//setTitle("").
        //setMessage(R.string.playlist_dir_notification_message).
        setView(dialogView).
        setCancelable(false).
        setPositiveButton(R.string.playlist_dir_notification_yes,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new SettingsManager(getContext()).setNotifyDisableDir(mNotifyDisableDir);
                mListener.onDirectoryClick(dir, true);
            }
        }).
        setNegativeButton(R.string.playlist_dir_notification_no,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new SettingsManager(getContext()).setNotifyDisableDir(mNotifyDisableDir);
            }
        }).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment
     */
    public interface OnFragmentInteractionListener {
        void onDirectoryClick(String dir, boolean state);
    }
}

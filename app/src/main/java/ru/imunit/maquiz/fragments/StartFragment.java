package ru.imunit.maquiz.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.imunit.maquiz.R;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StartFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button bPlaylists = (Button)getView().findViewById(R.id.playlists);
        bPlaylists.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playlistsClick();
                    }
                }
        );

        Button bPlay = (Button)getView().findViewById(R.id.play);
        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPlay();
                }
            }
        });

        Button bStats = (Button)getView().findViewById(R.id.stats);
        bStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onStatsOpen();
                }
            }
        });

        Button bRate = (Button)getView().findViewById(R.id.rateApp);
        bRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onRateApp();
                }
            }
        });

        Button bShare = (Button)getView().findViewById(R.id.shareApp);
        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onShareApp();
                }
            }
        });

//        ActivityManager am = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
//        int memoryClass = am.getMemoryClass();
//        Log.d("DEBUG", "memoryClass:" + Integer.toString(memoryClass));
    }

    private void playlistsClick() {
        if (mListener != null) {
            mListener.onPlaylistOpen();
        }
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

    public interface OnFragmentInteractionListener {
        void onPlaylistOpen();
        void onPlay();
        void onStatsOpen();
        void onRateApp();
        void onShareApp();
    }
}

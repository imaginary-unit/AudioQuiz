package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.models.IGameModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsFragmentListener} interface
 * to handle interaction events.
 */
public class ResultsFragment extends Fragment {

    private ResultsFragmentListener mListener;
    private IGameModel mModel;
    private TextView mTextScore;
    private Button mBtnRestart;
    private Button mBtnStatistics;
    private Button mBtnMenu;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public void setModel(IGameModel model) {
        mModel = model;
    }

    public void updateResults() {
        mTextScore.setText(String.valueOf(mModel.getGameScore()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResultsFragmentListener) {
            mListener = (ResultsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ResultsFragmentListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextScore = (TextView)getView().findViewById(R.id.textScore);
        mBtnMenu = (Button)getView().findViewById(R.id.btnMenu);
        mBtnRestart = (Button)getView().findViewById(R.id.btnRestart);
        mBtnStatistics = (Button)getView().findViewById(R.id.btnStatistics);
        setButtonListeners();
        mListener.onResultsFragmentInitialized();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setButtonListeners() {
        mBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShowMenu();
            }
        });
        mBtnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRestartGame();
            }
        });
        mBtnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShowStatistics();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface ResultsFragmentListener {
        void onResultsFragmentInitialized();
        void onRestartGame();
        void onShowStatistics();
        void onShowMenu();
    }
}

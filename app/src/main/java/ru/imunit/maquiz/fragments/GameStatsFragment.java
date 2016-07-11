package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.models.IStatsModel;
import ru.imunit.maquiz.models.StatsModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameStatsListener} interface
 * to handle interaction events.
 */
public class GameStatsFragment extends Fragment implements
        StatsModel.ModelUpdateListener {


    public interface GameStatsListener {
        void onGameStatsInitialized();
    }

    public GameStatsFragment() {
        // Required empty public constructor
    }

    public void setModel(IStatsModel model) {
        mModel = model;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("GameStats", "DBG CV");
        return inflater.inflate(R.layout.fragment_game_stats, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameStatsListener) {
            mListener = (GameStatsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameStatsListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mListener != null)
            mListener.onGameStatsInitialized();
        Log.i("GameStats", "DBG View created");
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
        updateContent();

    }

    private void updateContent() {
        // update top scores
        List<Integer> topScores = mModel.getTopScores();
        GridLayout gl = (GridLayout)getView().findViewById(R.id.topScoresLayout);
        // remove all views from top scores layout (except the title textview)
        int n = gl.getChildCount();
        for (int i=n-1; i >= 0; i--) {
            View v = gl.getChildAt(i);
            if (v.getId() != R.id.topScoresTitle) {
                gl.removeView(v);
            }
        }
        // add new line for each top score entry
        n = topScores.size();
        for (int i=0; i < n; i++) {
            TextView tNum = new TextView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.bottomMargin = 5;
            params.rightMargin = 5;
            params.columnSpec = GridLayout.spec(0);
            params.rowSpec = GridLayout.spec(i+1);
            tNum.setLayoutParams(params);
            tNum.setTextColor(getResources().getColor(R.color.colorForeground));
            tNum.setText(String.format(Locale.ENGLISH, "# %d.", i+1));
            TextView tScore = new TextView(getContext());
            params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.bottomMargin = 5;
            params.leftMargin = 5;
            params.columnSpec = GridLayout.spec(1);
            params.rowSpec = GridLayout.spec(i+1);
            tScore.setLayoutParams(params);
            tScore.setTextColor(getResources().getColor(R.color.colorForeground));
            tScore.setText(String.valueOf(topScores.get(i)));
            gl.addView(tNum);
            gl.addView(tScore);
        }
        // update all parameters
        TextView gc = (TextView)getView().findViewById(R.id.textGamesCount);
        TextView cgc = (TextView)getView().findViewById(R.id.textCleanGamesCount);
        TextView cgr = (TextView)getView().findViewById(R.id.textCorrectGuessRatio);
        TextView ags = (TextView)getView().findViewById(R.id.textAverageScore);
        TextView lfgr = (TextView)getView().findViewById(R.id.textLongestFastGuessRow);
        gc.setText(String.valueOf(mModel.getGamesCount()));
        cgc.setText(String.valueOf(mModel.getCleanGamesCount()));
        cgr.setText(String.format(Locale.ENGLISH, "%d%%",
                (int)(mModel.getCorrectGuessRatio() * 100)));
        ags.setText(String.valueOf(mModel.getAverageScore()));
        lfgr.setText(String.valueOf(mModel.getLongestFastGuessRow()));
    }

    private GameStatsListener mListener;
    private IStatsModel mModel;
}

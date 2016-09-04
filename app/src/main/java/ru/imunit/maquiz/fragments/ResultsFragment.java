package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.models.IGameModel;


public class ResultsFragment extends Fragment {

    private ResultsFragmentListener mListener;
    private IGameModel mModel;
    private TextView mTextScore;
    private ImageButton mBtnRestart;
    private ImageButton mBtnStatistics;
    private ImageButton mBtnMenu;
    private TextView mTextCongrats;
    private TextView mTextClean;
    private TextView mTextNewRecord;
    private TextView mTextHighscore;
    private TextView mTextTip;


    public interface ResultsFragmentListener {
        void onResultsFragmentInitialized();
        void onRestartGame();
        void onShowStatistics();
        void onShowMenu();
    }

    public ResultsFragment() {
        // Required empty public constructor
    }

    public void setModel(IGameModel model) {
        mModel = model;
    }

    public void updateResults() {
        long curScore = mModel.getGameScore();
        long highScore = mModel.getLastHighscore();
        float ratio = (float)curScore / (float)highScore;

        mTextScore.setText(String.valueOf(curScore));
        mTextHighscore.setText(String.valueOf(highScore));

        String congrat = "";
        if (ratio > 1f) {
            congrat = getResources().getString(R.string.game_results_congrats_3);
            mTextNewRecord.setVisibility(View.VISIBLE);
            mTextHighscore.setText(String.valueOf(curScore));
        }
        else if (ratio >= 0.75f)
            congrat = getResources().getString(R.string.game_results_congrats_2);
        else if (ratio >= 0.5f)
            congrat = getResources().getString(R.string.game_results_congrats_1);
        else if (ratio >= 0.1f)
            congrat = getResources().getString(R.string.game_results_congrats_0);
        else
            congrat = getResources().getString(R.string.game_results_congrats_bad);

        mTextCongrats.setText(congrat);

        if (mModel.isGameClean()) {
            mTextClean.setVisibility(View.VISIBLE);
        }

        String[] tips = getResources().getStringArray(R.array.game_results_tips);
        int idx = new Random().nextInt(tips.length);
        mTextTip.setText(Html.fromHtml(getString(R.string.game_results_tip_title) + tips[idx]));
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
        mTextHighscore = (TextView)getView().findViewById(R.id.textHighScore);
        mTextCongrats = (TextView)getView().findViewById(R.id.text1_congrats);
        mTextClean = (TextView)getView().findViewById(R.id.text2_clean);
        mTextNewRecord = (TextView)getView().findViewById(R.id.text3_new_record);
        mTextTip = (TextView)getView().findViewById(R.id.textTip);
        mBtnMenu = (ImageButton)getView().findViewById(R.id.btnMenu);
        mBtnRestart = (ImageButton)getView().findViewById(R.id.btnRestart);
        mBtnStatistics = (ImageButton)getView().findViewById(R.id.btnStatistics);
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

}

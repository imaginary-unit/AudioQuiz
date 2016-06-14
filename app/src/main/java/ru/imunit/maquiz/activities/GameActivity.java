package ru.imunit.maquiz.activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.fragments.GameFragment;
import ru.imunit.maquiz.fragments.ResultsFragment;
import ru.imunit.maquiz.models.GameModel;
import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.entities.DBTrack;

public class GameActivity extends AppCompatActivity
        implements GameFragment.GameFragmentListener,
        ResultsFragment.ResultsFragmentListener {

    private GameFragment mGameFragment;
    private ResultsFragment mResultsFragment;
    private GameModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mGameFragment = (GameFragment)getSupportFragmentManager().findFragmentByTag("GAMEFRAGMENT");

        if (mGameFragment == null) {
            mGameFragment = new GameFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, mGameFragment, "GAMEFRAGMENT");
            ft.commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    private void initModel() {
        if (mGameFragment.getModel() != null) {
            Log.i("Model init", "Game is already in progress!");
            return;
        }
        Log.i("Model init", "Create new and init game");
        mModel = new GameModel(DataSourceFactory.getDataSource(this));
        mGameFragment.setModel(mModel);
        mModel.subscribe(mGameFragment);
        // TODO: refactor options and rounds: should be in activity parameters
        mModel.initGame(5, 3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu here
        return super.onCreateOptionsMenu(menu);
    }

    // GameFragment listener

    @Override
    public void onGameFragmentInitialized() {
        initModel();
    }

    @Override
    public void onNextRound() {
        mModel.nextRound();
    }

    @Override
    public void onStartPlayback() {
        mModel.startPlayback();
    }

    @Override
    public void onMediaReady() {
        mModel.startTimer();
    }

    @Override
    public void onMakeGuess(DBTrack track) {
        mModel.makeGuess(track);
    }

    @Override
    public void onGameFinished() {
        if (mResultsFragment == null)
            mResultsFragment = new ResultsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, mResultsFragment, "RESULTSFRAGMENT");
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    // ResultsFragment listener

    @Override
    public void onResultsFragmentInitialized() {
        mResultsFragment.setModel(mModel);
        mResultsFragment.updateResults();
    }

    @Override
    public void onRestartGame() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, mGameFragment, "GAMEFRAGMENT");
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
        mModel.initGame(5, 3);
    }

    @Override
    public void onShowStatistics() {

    }

    @Override
    public void onShowMenu() {
        Intent menuIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.START_ACTIVITY));
        startActivity(menuIntent);
    }
}

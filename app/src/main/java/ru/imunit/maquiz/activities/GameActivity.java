package ru.imunit.maquiz.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.exceptions.DatabaseException;
import ru.imunit.maquiz.exceptions.NoMusicException;
import ru.imunit.maquiz.fragments.GameFragment;
import ru.imunit.maquiz.fragments.ModelRetainFragment;
import ru.imunit.maquiz.fragments.ResultsFragment;
import ru.imunit.maquiz.managers.ExceptionNotifier;
import ru.imunit.maquiz.managers.SettingsManager;
import ru.imunit.maquiz.models.GameModel;
import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.entities.DBTrack;

public class GameActivity extends AppCompatActivity
        implements GameFragment.GameFragmentListener,
        ResultsFragment.ResultsFragmentListener {

    private static final int OPTIONS_COUNT = 5;
    private static final int ROUNDS_COUNT = 10;

    private ModelRetainFragment mModelRetainFragment;
    private GameFragment mGameFragment;
    private ResultsFragment mResultsFragment;
    private GameModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // retain or create model
        mModelRetainFragment = (ModelRetainFragment)
                getSupportFragmentManager().findFragmentByTag("ModelRetain");
        if (mModelRetainFragment == null) {
            mModelRetainFragment = new ModelRetainFragment();
            mModelRetainFragment.setModel(new GameModel(DataSourceFactory.getDataSource(this)));
            getSupportFragmentManager().beginTransaction()
                    .add(mModelRetainFragment, "ModelRetain").commit();
            mModelRetainFragment.getModel().setMetronomeEnabled(
                    new SettingsManager(this).getMetronomeState());
        }
        mModel = mModelRetainFragment.getModel();
        mModel.setAsyncExceptionListener(new GameModel.AsyncExceptionListener() {
            @Override
            public void onDatabaseException() {
                ExceptionNotifier.make(findViewById(R.id.activity_game),
                        getResources().getString(R.string.err_database_error)).show();
            }
        });

        if (mModel.isGameFinished()) {
            showResultsFragment();
        } else if (mModel.isGameRunning()) {
            showGameFragment();
        } else {
            // TODO: refactor options and rounds: should be in activity parameters
            mModel.initGame(OPTIONS_COUNT, ROUNDS_COUNT);
            showGameFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu here
        return super.onCreateOptionsMenu(menu);
    }

    private void showGameFragment() {
        // try to retain the game fragment before creating a new instance
        mGameFragment = (GameFragment)getSupportFragmentManager().findFragmentByTag("GameFragment");
        if (mGameFragment == null)
            mGameFragment = new GameFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, mGameFragment, "GameFragment");
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void showResultsFragment() {
        // unsubscribe game fragment from model if it is added
        GameFragment gf = (GameFragment)getSupportFragmentManager().findFragmentByTag("GameFragment");
        if (gf != null)
            mModel.unsubscribe(gf);

        if (mResultsFragment == null)
            mResultsFragment = new ResultsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, mResultsFragment, "ResultsFragment");
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void nextRound() {
        try {
            mModel.nextRound();
        } catch (DatabaseException e) {
            ExceptionNotifier.make(findViewById(R.id.activity_game),
                    getResources().getString(R.string.err_database_error)).show();
        } catch (NoMusicException e) {
            ExceptionNotifier.make(findViewById(R.id.activity_game),
                    getResources().getString(R.string.err_no_music)).
                    setActionListener(new ExceptionNotifier.ActionListener() {
                @Override
                public void onClick() {
                    exitToMenu();
                }
            }).show();
        }
    }

    private void exitToMenu() {
        Intent startIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.START_ACTIVITY));
        startActivity(startIntent);
    }

    // GameFragment listener

    @Override
    public void onGameFragmentInitialized() {
        if (mGameFragment.getModel() == null) {
            mGameFragment.setModel(mModel);
            mModel.subscribe(mGameFragment);
        }
        if (!mModel.isGameRunning()) {
            nextRound();
        }
    }

    @Override
    public void onNextRound() {
        nextRound();
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
        showResultsFragment();
    }

    @Override
    public void onMusicReadError() {
        ExceptionNotifier.make(findViewById(R.id.activity_game),
                getResources().getString(R.string.err_music_io)).
                setActionListener(new ExceptionNotifier.ActionListener() {
                    @Override
                    public void onClick() {
                        exitToMenu();
                    }
                }).show();
    }

    // ResultsFragment listener

    @Override
    public void onResultsFragmentInitialized() {
        mResultsFragment.setModel(mModel);
        mResultsFragment.updateResults();
    }

    @Override
    public void onRestartGame() {
        mModel.initGame(OPTIONS_COUNT, ROUNDS_COUNT);
        showGameFragment();
    }

    @Override
    public void onShowStatistics() {
        Intent statsIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.STATS_ACTIVTY));
        startActivity(statsIntent);
        finish();
    }

    @Override
    public void onShowMenu() {
        Intent menuIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.START_ACTIVITY));
        startActivity(menuIntent);
        finish();
    }
}

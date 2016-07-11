package ru.imunit.maquiz.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.fragments.GameStatsFragment;
import ru.imunit.maquiz.fragments.TrackStatsFragment;
import ru.imunit.maquiz.models.StatsModel;
import ru.imunit.maquiz.views.adapters.StatsPagerAdapter;
import ru.imunit.maquizdb.DataSourceFactory;

public class StatsActivity extends AppCompatActivity implements
    GameStatsFragment.GameStatsListener, TrackStatsFragment.TrackStatsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int tab = StatsPagerAdapter.GAMES_TAB;
        if (savedInstanceState != null) {
            tab = savedInstanceState.getInt(SAVED_TAB);
            mGameStatsFragment = (GameStatsFragment)getSupportFragmentManager().
                    getFragment(savedInstanceState, GameStatsFragment.class.getName());
            mTrackStatsFragment = (TrackStatsFragment)getSupportFragmentManager().
                    getFragment(savedInstanceState, TrackStatsFragment.class.getName());
        }
        if (mGameStatsFragment == null)
            mGameStatsFragment = new GameStatsFragment();
        if (mTrackStatsFragment == null)
            mTrackStatsFragment = new TrackStatsFragment();

        setContentView(R.layout.activity_stats);
        // set toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.stats_toolbar_title);
        setSupportActionBar(toolbar);
        // init content
        gsInitialized = false;
        tsInitialized = false;
        initTabs(tab);
        initModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stats_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            clearStats();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_TAB, mViewPager.getCurrentItem());
        getSupportFragmentManager().putFragment(outState,
                GameStatsFragment.class.getName(), mGameStatsFragment);
        getSupportFragmentManager().putFragment(outState,
                TrackStatsFragment.class.getName(), mTrackStatsFragment);
    }

    @Override
    public void onGameStatsInitialized() {
        gsInitialized = true;
        if (tsInitialized)
            mModel.startUpdate();
    }

    @Override
    public void onTrackStatsInitialized() {
        tsInitialized = true;
        if (gsInitialized)
            mModel.startUpdate();
    }

//    private String getFragmentTag(int pos) {
//        return "android:switcher:"+R.id.viewPager+":"+pos;
//    }

    private void initTabs(int defaultTab) {
        Map<Integer, Fragment> tabs = new HashMap<>();
        tabs.put(StatsPagerAdapter.GAMES_TAB, mGameStatsFragment);
        tabs.put(StatsPagerAdapter.TRACKS_TAB, mTrackStatsFragment);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        StatsPagerAdapter adapter = new StatsPagerAdapter(this, getSupportFragmentManager(), tabs);
        mViewPager.setAdapter(adapter);
        TabLayout tl = (TabLayout)findViewById(R.id.tabLayout);
        tl.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(defaultTab);
    }

    private void initModel() {
        mModel = new StatsModel(DataSourceFactory.getDataSource(this));
        mGameStatsFragment.setModel(mModel);
        mTrackStatsFragment.setModel(mModel);
        mModel.subscribe(mGameStatsFragment);
        mModel.subscribe(mTrackStatsFragment);
    }

    private void clearStats() {
        mModel.startClear();
    }

    private boolean gsInitialized;
    private boolean tsInitialized;
    private final String SAVED_TAB = "savedTab";
    private ViewPager mViewPager;
    private StatsModel mModel;
    private GameStatsFragment mGameStatsFragment;
    private TrackStatsFragment mTrackStatsFragment;
}

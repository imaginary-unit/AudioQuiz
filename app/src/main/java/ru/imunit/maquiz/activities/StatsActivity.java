package ru.imunit.maquiz.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

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
        // retain the last tab
        int tab = StatsPagerAdapter.GAMES_TAB;
        if (savedInstanceState != null) {
            tab = savedInstanceState.getInt(SAVED_TAB);
        }
        setContentView(R.layout.activity_stats);
        // set toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.stats_toolbar_title);
        // init content
        gsInitialized = false;
        tsInitialized = false;
        initTabs(tab);
        initModel();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_TAB, mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
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

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mGameStatsFragment != null)
//            mModel.unsubscribe(mGameStatsFragment);
//        if (mTrackStatsFragment != null)
//            mModel.unsubscribe(mTrackStatsFragment);
//    }

//    private String getFragmentTag(int pos) {
//        return "android:switcher:"+R.id.viewPager+":"+pos;
//    }

    private void initTabs(int defaultTab) {
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        StatsPagerAdapter adapter = new StatsPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        TabLayout tl = (TabLayout)findViewById(R.id.tabLayout);
        tl.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(defaultTab);
        mGameStatsFragment = (GameStatsFragment)adapter.getItem(StatsPagerAdapter.GAMES_TAB);
        mTrackStatsFragment = (TrackStatsFragment)adapter.getItem(StatsPagerAdapter.TRACKS_TAB);
    }

    private void initModel() {
        mModel = new StatsModel(DataSourceFactory.getDataSource(this));
        mGameStatsFragment.setModel(mModel);
        mTrackStatsFragment.setModel(mModel);
        mModel.subscribe(mGameStatsFragment);
        mModel.subscribe(mTrackStatsFragment);
    }

    private boolean gsInitialized;
    private boolean tsInitialized;
    private final String SAVED_TAB = "savedTab";
    private ViewPager mViewPager;
    private StatsModel mModel;
    private GameStatsFragment mGameStatsFragment;
    private TrackStatsFragment mTrackStatsFragment;
}

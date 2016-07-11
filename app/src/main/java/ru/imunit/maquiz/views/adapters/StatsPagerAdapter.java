package ru.imunit.maquiz.views.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.fragments.GameStatsFragment;
import ru.imunit.maquiz.fragments.TrackStatsFragment;

/**
 * Created by theuser on 09.07.16.
 */

public class StatsPagerAdapter extends FragmentPagerAdapter {

    public static final int GAMES_TAB = 0;
    public static final int TRACKS_TAB = 1;

    public StatsPagerAdapter(Context context, FragmentManager fm, Map<Integer, Fragment> tabs) {
        super(fm);
        mContext = context;
        mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == GAMES_TAB)
            return mContext.getString(R.string.stats_games_tab);
        else if (position == TRACKS_TAB)
            return mContext.getString(R.string.stats_tracks_tab);
        else
            return "";
    }

//    private void initTabs() {
//        Log.i("DBG Adapter", "creating tabs");
////        mTabs = new HashMap<>();
////        mTabs.put(GAMES_TAB, new GameStatsFragment());
////        mTabs.put(TRACKS_TAB, new TrackStatsFragment());
//    }

    private Context mContext;
    private Map<Integer, Fragment> mTabs;
}

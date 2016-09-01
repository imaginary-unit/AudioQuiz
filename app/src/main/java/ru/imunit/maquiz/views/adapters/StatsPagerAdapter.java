package ru.imunit.maquiz.views.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Map;

import ru.imunit.maquiz.R;


public class StatsPagerAdapter extends FragmentPagerAdapter {

    public static final int GAMES_TAB = 0;
    public static final int TRACKS_TAB = 1;

    private Context mContext;
    private Map<Integer, Fragment> mTabs;


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
}

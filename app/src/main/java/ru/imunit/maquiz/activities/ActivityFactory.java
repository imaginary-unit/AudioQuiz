package ru.imunit.maquiz.activities;

/**
 * Created by smirnov on 16.10.2015.
 */
public class ActivityFactory {

    public static final int START_ACTIVITY = 1;
    public static final int PLAYLIST_ACTIVITY = 2;
    public static final int GAME_ACTIVTY = 3;
    public static final int STATS_ACTIVTY = 4;

    public static Class<?> getActivity(int id) {
        switch (id) {
            case START_ACTIVITY: return StartActivity.class;
            case PLAYLIST_ACTIVITY: return PlaylistViewerActivity.class;
            case GAME_ACTIVTY: return GameActivity.class;
            case STATS_ACTIVTY: return StatsActivity.class;
            default: return null;
        }
    }

}

package ru.imunit.maquiz.activities;

/**
 * Created by smirnov on 16.10.2015.
 */
public class ActivityFactory {

    public static final int START_SCREEN = 1;
    public static final int PLAYLISTS_VIEWER = 2;
    public static final int PLAYLIST_EDITOR = 3;
    public static final int GAME_SCREEN = 4;
    public static final int RESULTS_SCREEN = 5;

    public static Class<?> getActivity(int id) {
        switch (id) {
            case START_SCREEN: return StartActivity.class;
            case PLAYLISTS_VIEWER: return PlaylistViewerActivity.class;
            case GAME_SCREEN:
            case RESULTS_SCREEN:
            default: return null;
        }
    }

}

package ru.imunit.maquiz.managers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lemoist on 10.08.16.
 */

public class SettingsManager {

    public SettingsManager(Context context) {
        mContext = context;
    }

    public void setVisualizerState(boolean state) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(VISUALIZER_STATE, state);
        editor.commit();
    }

    public void setMetronomeState(boolean state) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(METRONOME_STATE, state);
        editor.commit();
    }

    public void setFewMusicNotified(boolean state) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FEW_MUSIC_NOTIFIED, state);
        editor.commit();
    }

    public boolean getVisualizerState() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        return sp.getBoolean(VISUALIZER_STATE, true);
    }

    public boolean getMetronomeState() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        return sp.getBoolean(METRONOME_STATE, true);
    }

    public boolean getFewMusicNorified() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        return sp.getBoolean(FEW_MUSIC_NOTIFIED, false);
    }

    private Context mContext;

    private static final String PREFS_NAME = "GamePreferences";
    private static final String VISUALIZER_STATE = "visualizerState";
    private static final String METRONOME_STATE = "metronomeState";
    private static final String FEW_MUSIC_NOTIFIED = "fewMusicNotified";
}

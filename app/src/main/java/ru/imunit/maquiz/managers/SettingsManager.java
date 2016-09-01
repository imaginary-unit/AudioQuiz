package ru.imunit.maquiz.managers;

import android.content.Context;
import android.content.SharedPreferences;


public class SettingsManager {

    private Context mContext;

    private static final String PREFS_NAME = "GamePreferences";
    private static final String METRONOME_STATE = "metronomeState";
    private static final String FEW_MUSIC_NOTIFIED = "fewMusicNotified";
    private static final String TOOLTIPS_SHOWN = "showTooltips";

    public SettingsManager(Context context) {
        mContext = context;
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

    public void setTooltipsShown(boolean state) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(TOOLTIPS_SHOWN, state);
        editor.commit();
    }

    public boolean getMetronomeState() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        return sp.getBoolean(METRONOME_STATE, true);
    }

    public boolean getFewMusicNotified() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        return sp.getBoolean(FEW_MUSIC_NOTIFIED, false);
    }

    public boolean getTooltipsShown() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        return sp.getBoolean(TOOLTIPS_SHOWN, true);
    }
}

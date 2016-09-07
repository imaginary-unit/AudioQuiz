package ru.imunit.maquiz.managers;

import android.content.Context;
import android.content.SharedPreferences;


public class SettingsManager {

    private Context mContext;

    private static final String PREFS_NAME = "GamePreferences";
    private static final String METRONOME_STATE = "metronomeState";
    private static final String FEW_MUSIC_NOTIFIED = "fewMusicNotified";
    private static final String TOOLTIPS_SHOWN = "showTooltips";
    private static final String ADS_ENABLES = "adsEnabled";
    private static final String NOTIFY_DISABLE_DIR = "notifyDisableDir";

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

    public void setAdsEnables(boolean state) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(ADS_ENABLES, state);
        editor.commit();
    }

    public void setNotifyDisableDir(boolean state) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(NOTIFY_DISABLE_DIR, state);
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

    public boolean getAdsEnabled() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        return sp.getBoolean(ADS_ENABLES, true);
    }

    public boolean getNotifyDisableDir() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, 0);
        return sp.getBoolean(NOTIFY_DISABLE_DIR, true);
    }
}

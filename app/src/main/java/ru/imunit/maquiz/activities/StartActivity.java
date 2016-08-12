package ru.imunit.maquiz.activities;

import android.Manifest;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.fragments.StartFragment;
import ru.imunit.maquiz.managers.ExceptionNotifier;
import ru.imunit.maquiz.managers.MusicUpdater;
import ru.imunit.maquiz.managers.SettingsManager;

public class StartActivity extends AppCompatActivity
        implements StartFragment.OnFragmentInteractionListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 2;
    private View mRootLayout;
    private ProgressDialog mProgress = null;
    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRootLayout = findViewById(R.id.activity_start);
        if (checkStoragePermission()) {
            // TODO: maybe do music update only on first activity show
            startMusicUpdate();
        }
        checkRecordPermission();
    }

    @Override
    public void onPlaylistOpen() {
        if (!checkStoragePermission())
            return;
        Intent playlistsIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.PLAYLIST_ACTIVITY));
        startActivity(playlistsIntent);
    }

    @Override
    public void onPlay() {
        if (!checkStoragePermission())
            return;
        Intent gameIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.GAME_ACTIVTY));
        startActivity(gameIntent);
    }

    @Override
    public void onStatsOpen() {
        if (!checkStoragePermission())
            return;
        Intent statsIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.STATS_ACTIVTY));
        startActivity(statsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SettingsManager sm = new SettingsManager(this);
        menu.findItem(R.id.action_metronome).setChecked(sm.getMetronomeState());
        menu.findItem(R.id.action_visualizer).setChecked(sm.getVisualizerState());
        mOptionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_metronome) {
            SettingsManager sm = new SettingsManager(this);
            boolean newState = !sm.getMetronomeState();
            sm.setMetronomeState(newState);
            item.setChecked(newState);
            Log.d("Metronome state", String.valueOf(sm.getMetronomeState()));
            return true;
        }
        else if (item.getItemId() == R.id.action_visualizer) {
            SettingsManager sm = new SettingsManager(this);
            boolean newState = !sm.getVisualizerState();
            if (checkRecordPermission()) {
                sm.setVisualizerState(newState);
                item.setChecked(newState);
                Log.d("Visualizer state", String.valueOf(sm.getVisualizerState()));
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(mRootLayout, R.string.permission_storage_rationale,
                        Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(StartActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_READ_STORAGE);
                    }
                }).show();
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_STORAGE);
            }
            return false;
        }
        else {
            return true;
        }
    }

    private boolean checkRecordPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
            return false;
        }
        else {
            return true;
        }
    }

    private void startMusicUpdate() {
        mProgress = ProgressDialog.show(this, null,
                getResources().getString(R.string.updating_music_dialog), true, true);
        MusicUpdater updater = new MusicUpdater(this);
        updater.setListener(new MusicUpdater.MusicUpdateListener() {
            @Override
            public void onUpdateCompleted(int res) {
                if (StartActivity.this.mProgress != null) {
                    StartActivity.this.mProgress.dismiss();
                }
                if (res == MusicUpdater.RESULT_ERROR) {
                    ExceptionNotifier.make(mRootLayout,
                            getResources().getString(R.string.err_database_error)).show();
                } else if (res == MusicUpdater.RESULT_FEW_MUSIC) {
                    SettingsManager sm = new SettingsManager(StartActivity.this);
                    if (!sm.getFewMusicNorified()) {
                        sm.setFewMusicNotified(true);
                        String few_mus = String.format(Locale.ENGLISH,
                                getResources().getString(R.string.err_few_music),
                                MusicUpdater.FEW_MUSIC_THRESHOLD);
                        ExceptionNotifier.make(mRootLayout, few_mus).
                                setActionListener(new ExceptionNotifier.ActionListener() {
                                    @Override
                                    public void onClick() {
                                        // added handler just to have dismiss button in snackbar
                                    }
                                }).show();
                    }
                } else if (res == MusicUpdater.RESULT_NO_MUSIC) {
                    ExceptionNotifier.make(mRootLayout,
                            getResources().getString(R.string.err_no_music)).show();
                }
            }
        });
        updater.startUpdate();

    }

    private void noPermissionsExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.err_permissions_title).setMessage(R.string.err_permissions_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StartActivity.this.finishAffinity();
            }
        });
        AlertDialog d = builder.create();
        d.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
            if (grantResults.length == 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                noPermissionsExit();
            } else {
                startMusicUpdate();
            }
        }
        else if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length == 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // if no record permissions, show the notification and disable the visualizer
                Snackbar.make(mRootLayout, R.string.permission_record_rationale,
                        Snackbar.LENGTH_LONG).show();
                new SettingsManager(this).setVisualizerState(false);
                if (mOptionsMenu != null) {
                    mOptionsMenu.findItem(R.id.action_visualizer).setChecked(false);
                }
            }
            else {
                new SettingsManager(this).setVisualizerState(true);
                if (mOptionsMenu != null) {
                    mOptionsMenu.findItem(R.id.action_visualizer).setChecked(true);
                }
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}

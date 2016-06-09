package ru.imunit.maquiz.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.fragments.StartFragment;

public class StartActivity extends AppCompatActivity
        implements StartFragment.OnFragmentInteractionListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;
    private View mRootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRootLayout = findViewById(R.id.start_layout);
        checkStoragePermission();
    }

    @Override
    public void onPlaylistOpen() {
        Intent playlistsIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.PLAYLIST_ACTIVITY));
        startActivity(playlistsIntent);
    }

    @Override
    public void onPlay() {
        Intent gameIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.GAME_ACTIVTY));
        startActivity(gameIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void checkStoragePermission() {
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
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
//            if (grantResults.length == 0
//                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//
//            }
//        }
//        else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

}

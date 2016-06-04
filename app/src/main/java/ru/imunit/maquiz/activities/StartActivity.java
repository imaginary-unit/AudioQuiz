package ru.imunit.maquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.fragments.StartFragment;

public class StartActivity extends AppCompatActivity
        implements StartFragment.OnFragmentInteractionListener {

    // private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    public void onPlaylistOpen() {
        Intent playlistsIntent = new Intent(this,
                ActivityFactory.getActivity(ActivityFactory.PLAYLISTS_VIEWER));
        startActivity(playlistsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

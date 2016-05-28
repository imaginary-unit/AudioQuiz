package ru.imunit.maquiz.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.imunit.maquiz.managers.MusicUpdater;
import ru.imunit.maquiz.R;
import ru.imunit.maquiz.views.adapters.PlaylistRecyclerAdapter;
import ru.imunit.maquizdb.entities.DBTrack;
import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.IDataSource;

public class PlaylistViewerActivity extends Activity {

    Toolbar mToolbar;
    RecyclerView mRecycler;
    RecyclerView.Adapter mRecyclerAdapter;
    RecyclerView.LayoutManager mRecyclerLayout;

    List<DBTrack> mTrackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        initToolbar();
        updateMusic();
        initRecycler();
    }


    private void initRecycler() {
        IDataSource dataSource = DataSourceFactory.getDataSource(this);
        // TODO: handle exception
        dataSource.openReadable();
        mTrackList = new ArrayList<>(Arrays.asList(dataSource.getAllTracks()));

        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mRecycler.setHasFixedSize(true);
        mRecyclerLayout = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mRecyclerLayout);
        mRecyclerAdapter = new PlaylistRecyclerAdapter(mTrackList);
        mRecycler.setAdapter(mRecyclerAdapter);
    }

    private void initToolbar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.playlists_title);
        mToolbar.inflateMenu(R.menu.menu_main);
    }

    public void onFabClick(View view) {
        Toast.makeText(this, "FAB clicked", Toast.LENGTH_SHORT).show();
//        mModel.addPlaylist("New playlist");
    }

    private void updateMusic() {
        MusicUpdater updater = new MusicUpdater(this);
        updater.updateSync();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}

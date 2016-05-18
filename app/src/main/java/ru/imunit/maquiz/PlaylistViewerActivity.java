package ru.imunit.maquiz;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import ru.imunit.maquiz.R;

public class PlaylistViewerActivity extends Activity {

    Toolbar mToolbar;
//    RecyclerView mRecycler;
//    RecyclerView.Adapter mRecyclerAdapter;
//    RecyclerView.LayoutManager mRecyclerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        initToolbar();
        getAllMusic();
//        initRecycler();
    }


//    private void initRecycler() {
//        mRecycler = (RecyclerView)findViewById(R.id.recycler);
//        mRecycler.setHasFixedSize(true);
//        mRecyclerLayout = new LinearLayoutManager(this);
//        mRecycler.setLayoutManager(mRecyclerLayout);
//        mRecyclerAdapter = new PlaylistRecyclerAdapter(mModel.getPlaylists());
//        mRecycler.setAdapter(mRecyclerAdapter);
//    }

    private void initToolbar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.playlists_title);
        mToolbar.inflateMenu(R.menu.menu_main);
    }

    public void onFabClick(View view) {
        Toast.makeText(this, "FAB clicked", Toast.LENGTH_SHORT).show();
//        mModel.addPlaylist("New playlist");
    }

    private void onEditPlaylist() {

    }

    private void getAllMusic() {
        String[] STAR = { "*" };
        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cur = getContentResolver().query(allSongsUri, STAR, selection, null, null);
        if (cur != null) {
            ArrayList<String> songs = new ArrayList<String>();
            if (cur.moveToFirst()) {
                do {
                    String s = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    songs.add(s);
                    Log.i("Song found:", s);
                } while (cur.moveToNext());
            }
            cur.close();
        }
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

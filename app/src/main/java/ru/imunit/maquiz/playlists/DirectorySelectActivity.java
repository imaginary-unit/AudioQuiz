package ru.imunit.maquiz.playlists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.imunit.maquiz.R;

public class DirectorySelectActivity extends Activity {

    Toolbar mToolbar;
    List<AudioDirectory> mDirectories;
    String mCurrentPath;
    String[] mSelectedDirs;

    private void getSelected() {

    }

    private void updateDirectories() {
        if (mDirectories == null)
            mDirectories = new ArrayList<AudioDirectory>();
        mDirectories.clear();

        File iDir = new File(mCurrentPath);
        File[] files = iDir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                File[] innerFiles = f.listFiles(new MusicFileFilter());
                int dirCnt = 0;
                int trackCnt = 0;
                for (File ff : innerFiles) {
                    if (ff.isDirectory()) dirCnt++;
                    else trackCnt++;
                }
                AudioDirectory ad = new AudioDirectory();
                ad.setName(f.getName());
                ad.setDirsCount(dirCnt);
                ad.setTracksCount(trackCnt);
                //ad.setState();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_select);
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_main);
    }

    public void onFabClick(View view) {
        //Intent intent = new Intent();

    }

}

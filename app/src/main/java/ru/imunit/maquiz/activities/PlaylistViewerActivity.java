package ru.imunit.maquiz.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.fragments.PlaylistDirsFragment;
import ru.imunit.maquiz.fragments.PlaylistTracksFragment;
import ru.imunit.maquiz.models.PlaylistModel;
import ru.imunit.maquizdb.DataSourceFactory;

public class PlaylistViewerActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        PlaylistTracksFragment.OnFragmentInteractionListener {

    private final int ALL_TRACKS = 0;
    private final int BLACK_LIST = 1;
    private final int MUSIC_DIRECTORIES = 2;
    private final String VIEW_MODE = "viewMode";

    private int mCurrentMode;
    private AppCompatSpinner mViewModeSpinner;
    private PlaylistModel mModel;

    private PlaylistDirsFragment mDirsFragment;
    private PlaylistTracksFragment mTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain current mode
        if (savedInstanceState != null) {
            mCurrentMode = savedInstanceState.getInt(VIEW_MODE);
        } else {
            mCurrentMode = ALL_TRACKS;
        }
        setContentView(R.layout.activity_playlist);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.playlist_title);

        mModel = new PlaylistModel(DataSourceFactory.getDataSource(this));

        mViewModeSpinner = new AppCompatSpinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.playlist_view_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
        mViewModeSpinner.setAdapter(adapter);
        toolbar.addView(mViewModeSpinner);
        Toolbar.LayoutParams tlp = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END);
        mViewModeSpinner.setLayoutParams(tlp);
        mViewModeSpinner.setSelection(mCurrentMode);
        mViewModeSpinner.setOnItemSelectedListener(this);
        setSupportActionBar(toolbar);

        // set corresponding fragment
        Fragment currentFragment;
        String currentTag;
        if (mCurrentMode == MUSIC_DIRECTORIES) {
            mDirsFragment = new PlaylistDirsFragment();
            currentFragment = mDirsFragment;
            currentTag = "DirsFragment";
        }
        else {
            mTracksFragment = new PlaylistTracksFragment();
            currentFragment = mTracksFragment;
            currentTag = "TracksFragment";
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, currentFragment, currentTag);
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(VIEW_MODE, mCurrentMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu here
        return super.onCreateOptionsMenu(menu);
    }



    // View mode spinner select listener implementation

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (mCurrentMode != i) {
            mCurrentMode = i;
            // show corresponding fragment
            mModel.initUpdate(this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

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

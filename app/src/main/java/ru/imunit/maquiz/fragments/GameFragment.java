package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import ru.imunit.maquiz.R;
import ru.imunit.maquiz.models.GameModel;
import ru.imunit.maquiz.models.IGameModel;
import ru.imunit.maquiz.views.widgets.TrackView;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragmentListener} interface
 * to handle interaction events.
 */
public class GameFragment extends Fragment
            implements GameModel.ModelUpdateListener {

    private IGameModel mModel;
    private GameFragmentListener mListener;
    private TextView mTextRound;
    private TextView mTextScore;
    private TextView mTextTime;
    private LinearLayout mTracksLayout;

//    private View getTrackView(DBTrack track) {
//        View v = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_item_2,
//                this.mTracksLayout, false);
//        ImageView iv = (ImageView)v.findViewById(R.id.icon);
//        iv.setVisibility(View.GONE);
//        TextView txtArtist = (TextView)v.findViewById(R.id.firstLine);
//        TextView txtTitle = (TextView)v.findViewById(R.id.secondLine);
//        txtArtist.setText(track.getArtist());
//        txtTitle.setText(track.getName());
//        return v;
//    }

    public GameFragment() {
        // Required empty public constructor
    }

    public void setModel(IGameModel model) {
        mModel = model;
    }

    public void setListener(GameFragmentListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameFragmentListener) {
            mListener = (GameFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameFragmentListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextRound = (TextView)getView().findViewById(R.id.textTracks);
        mTextScore = (TextView)getView().findViewById(R.id.textScore);
        mTextTime = (TextView)getView().findViewById(R.id.textTime);
        mTracksLayout = (LinearLayout)getView().findViewById(R.id.layoutTracks);
        mListener.onFragmentInitialized();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *  Model events listener
     */

    public void onRoundUpdated() {
        mTextRound.setText(String.format(Locale.ENGLISH, "%d / %d",
                mModel.getCurrentRound(), mModel.getRoundsCount()));
        mTextTime.setText(String.format(Locale.ENGLISH, "%.2f",
                ((float)mModel.getTimerData() / 1E3f)));
        mTextScore.setText(String.valueOf(mModel.getGameScore()));

        mTracksLayout.removeAllViews();
        List<DBTrack> tracks = mModel.getTracks();
        for (DBTrack track : tracks) {
            TrackView tv = new TrackView(getActivity());
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
            tv.setTrack(track);
            mTracksLayout.addView(tv);
        }

        // TODO: play metronome before playback
        mListener.onStartPlayback();
    }

    @Override
    public void onScoreUpdated(long diff) {
        mTextScore.setText(String.valueOf(mModel.getGameScore()));
        // TODO: show somewhere else...
        Toast.makeText(getActivity(), String.format(Locale.ENGLISH, "+ %d",
                mModel.getRoundScore()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerUpdated(long time) {
        mTextTime.setText(String.format(Locale.ENGLISH, "%.2f", (float)time / 1E3f));
    }

    @Override
    public void onGuessVerified(boolean result) {

    }

    @Override
    public void onPlaybackStarted(float position) {
        Toast.makeText(getActivity(), "Playback started..", Toast.LENGTH_SHORT).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface GameFragmentListener {
        void onFragmentInitialized();
        void onNextRound();
        void onStartPlayback();
        void onMakeGuess(DBTrack track);
    }
}

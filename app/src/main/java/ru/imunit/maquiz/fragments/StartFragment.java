package ru.imunit.maquiz.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import it.sephiroth.android.library.tooltip.Tooltip;
import ru.imunit.maquiz.R;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StartFragment extends Fragment implements
        View.OnClickListener, View.OnTouchListener {

    private final int TOOLTIP_DURATION = 1000;
    private final int TOOLTIP_DELAY = 200;
    private OnFragmentInteractionListener mListener;
    private ImageButton mPlaylists;
    private ImageButton mPlay;
    private ImageButton mStats;
    private ImageButton mShare;
    private ImageButton mRate;
    private Tooltip.TooltipView mTooltipPlaylists;
    private Tooltip.TooltipView mTooltipPlay;
    private Tooltip.TooltipView mTooltipStats;
    private Tooltip.TooltipView mTooltipShare;
    private Tooltip.TooltipView mTooltipRate;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPlaylists = (ImageButton)getView().findViewById(R.id.playlists);
        mPlaylists.setOnClickListener(this);
        mPlaylists.setOnTouchListener(this);

        mPlay = (ImageButton)getView().findViewById(R.id.play);
        mPlay.setOnClickListener(this);
        mPlay.setOnTouchListener(this);

        mStats = (ImageButton)getView().findViewById(R.id.stats);
        mStats.setOnClickListener(this);
        mStats.setOnTouchListener(this);

        mRate = (ImageButton)getView().findViewById(R.id.rateApp);
        mRate.setOnClickListener(this);
        mRate.setOnTouchListener(this);

        mShare = (ImageButton)getView().findViewById(R.id.shareApp);
        mShare.setOnClickListener(this);
        mShare.setOnTouchListener(this);

        makeTooltips();
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            if (view == mPlaylists)
                mListener.onPlaylistOpen();
            else if (view == mPlay)
                mListener.onPlay();
            else if (view == mStats)
                mListener.onStatsOpen();
            else if (view == mRate)
                mListener.onRateApp();
            else if (view == mShare)
                mListener.onShareApp();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent evt) {
        if (mListener != null && mListener.getTooltipState()) {
            if (evt.getAction() == MotionEvent.ACTION_DOWN) {
                if (view == mPlaylists)
                    mTooltipPlaylists.show();
                else if (view == mPlay)
                    mTooltipPlay.show();
                else if (view == mStats)
                    mTooltipStats.show();
                else if (view == mRate)
                    mTooltipRate.show();
                else if (view == mShare)
                    mTooltipShare.show();
            }
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void makeTooltips() {
        Tooltip.Gravity grav0;
        Tooltip.Gravity grav1;
        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            grav0 = Tooltip.Gravity.TOP;
            grav1 = Tooltip.Gravity.BOTTOM;
        }
        else {
            grav0 = Tooltip.Gravity.RIGHT;
            grav1 = Tooltip.Gravity.TOP;
        }

        // determine text size using screen size info
        int resId = R.style.ToolTipLayoutStyle;
        int screenLayout = getContext().getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            resId = R.style.ToolTipLayoutStyleXlarge;
        }

        mTooltipPlaylists = Tooltip.make(getContext(), new Tooltip.Builder().
                anchor(mPlaylists, grav0).
                closePolicy(Tooltip.ClosePolicy.TOUCH_ANYWHERE_CONSUME, TOOLTIP_DURATION).
                text(getString(R.string.tooltip_playlist)).
                fitToScreen(true).
                activateDelay(TOOLTIP_DELAY).
                withArrow(true).
                withStyleId(resId).
                build());

        mTooltipPlay = Tooltip.make(getContext(), new Tooltip.Builder().
                anchor(mPlay, grav0).
                closePolicy(Tooltip.ClosePolicy.TOUCH_ANYWHERE_CONSUME, TOOLTIP_DURATION).
                text(getString(R.string.tooltip_play)).
                fitToScreen(true).
                activateDelay(TOOLTIP_DELAY).
                withArrow(true).
                withStyleId(resId).
                build());

        mTooltipStats = Tooltip.make(getContext(), new Tooltip.Builder().
                anchor(mStats, grav0).
                closePolicy(Tooltip.ClosePolicy.TOUCH_ANYWHERE_CONSUME, TOOLTIP_DURATION).
                text(getString(R.string.tooltip_stats)).
                fitToScreen(true).
                activateDelay(TOOLTIP_DELAY).
                withArrow(true).
                withStyleId(resId).
                build());

        mTooltipRate = Tooltip.make(getContext(), new Tooltip.Builder().
                anchor(mRate, grav1).
                closePolicy(Tooltip.ClosePolicy.TOUCH_ANYWHERE_CONSUME, TOOLTIP_DURATION).
                text(getString(R.string.tooltip_rate)).
                fitToScreen(true).
                activateDelay(TOOLTIP_DELAY).
                withArrow(true).
                withStyleId(resId).
                build());

        mTooltipShare = Tooltip.make(getContext(), new Tooltip.Builder().
                anchor(mShare, grav1).
                closePolicy(Tooltip.ClosePolicy.TOUCH_ANYWHERE_CONSUME, TOOLTIP_DURATION).
                text(getString(R.string.tooltip_share)).
                fitToScreen(true).
                activateDelay(TOOLTIP_DELAY).
                withArrow(true).
                withStyleId(resId).
                build());
    }

    public interface OnFragmentInteractionListener {
        void onPlaylistOpen();
        void onPlay();
        void onStatsOpen();
        void onRateApp();
        void onShareApp();
        boolean getTooltipState();
    }
}

package ru.imunit.maquiz.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ru.imunit.maquiz.R;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by theuser on 09.06.16.
 */
public class TrackView extends FrameLayout {

    private DBTrack mTrack;
    private ImageView mIcon;
    private TextView mArtist;
    private TextView mTitle;

    public TrackView(Context context) {
        super(context);
        init();
    }

    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.recycler_item_2, this);
        mIcon = (ImageView)findViewById(R.id.icon);
        mArtist = (TextView)findViewById(R.id.firstLine);
        mTitle = (TextView)findViewById(R.id.secondLine);
        // mIcon.setVisibility(View.GONE);
    }

    public void setTrack(DBTrack track) {
        mTrack = track;
        mArtist.setText(track.getArtist());
        mTitle.setText(track.getName());
    }

    public DBTrack getTrack() {
        return mTrack;
    }
}

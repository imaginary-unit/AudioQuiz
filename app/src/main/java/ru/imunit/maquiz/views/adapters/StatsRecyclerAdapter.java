package ru.imunit.maquiz.views.adapters;

import android.content.Context;
import android.support.v4.content.ParallelExecutorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ru.imunit.maquiz.R;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by theuser on 10.07.16.
 */

public class StatsRecyclerAdapter extends
RecyclerView.Adapter<StatsRecyclerAdapter.PlaylistViewHolder> {

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView trackInfo;
        public TextView guessed;
        public TextView correct;
        public PlaylistViewHolder(View itemView) {
            super(itemView);
            this.trackInfo = (TextView)itemView.findViewById(R.id.trackInfo);
            this.guessed = (TextView)itemView.findViewById(R.id.guessed);
            this.correct = (TextView)itemView.findViewById(R.id.correct);
        }
    }

    public StatsRecyclerAdapter(Context context, List<DBTrack> dataset) {
        mDataset = dataset;
        mContext = context;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.stats_recycler_item, parent, false);
        return new PlaylistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        DBTrack track = mDataset.get(position);
        int perc = (int)(((float)track.getCorrectGuess() / (float)track.getGuess()) * 100.0f);
        holder.trackInfo.setText(String.format(Locale.ENGLISH, "%s - %s",
                track.getArtist(), track.getName()));
        holder.guessed.setText(String.format(Locale.ENGLISH, "%s: %d;",
                mContext.getResources().getString(R.string.stats_track_guessed),
                track.getGuess()));
        holder.correct.setText(String.format(Locale.ENGLISH, "%s: %d (%d%%)",
                mContext.getResources().getString(R.string.stats_track_correct),
                track.getCorrectGuess(), perc));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private List<DBTrack> mDataset;
    private Context mContext;
}

package ru.imunit.maquiz.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.imunit.maquiz.R;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 19.05.16.
 */
public class PlaylistRecyclerAdapter extends
        RecyclerView.Adapter<PlaylistRecyclerAdapter.PlaylistViewHolder> {

    List<DBTrack> mDataset;

    public PlaylistRecyclerAdapter(List<DBTrack> dataset) {
        this.mDataset = dataset;
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView artist;
        public TextView title;
        public PlaylistViewHolder(View itemView) {
            super(itemView);
            this.artist = (TextView)itemView.findViewById(R.id.firstLine);
            this.title = (TextView)itemView.findViewById(R.id.secondLine);
        }
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.recycler_item_2, parent, false);
        ImageView iv = (ImageView)v.findViewById(R.id.icon);
        iv.setImageResource(R.drawable.folder);
        return new PlaylistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int pos) {
        DBTrack track = mDataset.get(pos);
        holder.artist.setText(track.getArtist());
        holder.title.setText(track.getName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

package ru.imunit.maquiz;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.imunit.maquizdb.DBTrack;

/**
 * Created by lemoist on 19.05.16.
 */
public class PlaylistRecyclerAdapter extends
        RecyclerView.Adapter<PlaylistRecyclerAdapter.ViewHolder> {

    List<DBTrack> mDataset;

    public PlaylistRecyclerAdapter(List<DBTrack> dataset) {
        mDataset = dataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView artist;
        public TextView title;
        public ViewHolder(View itemView) {
            super(itemView);
            artist = (TextView)itemView.findViewById(R.id.firstLine);
            title = (TextView)itemView.findViewById(R.id.secondLine);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.recycler_item_2, parent, false);
        ImageView iv = (ImageView)v.findViewById(R.id.icon);
        iv.setImageResource(R.drawable.folder);
        return new ViewHolder(iv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        DBTrack track = mDataset.get(pos);
        holder.artist.setText(track.getArtist());
        holder.title.setText(track.getName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

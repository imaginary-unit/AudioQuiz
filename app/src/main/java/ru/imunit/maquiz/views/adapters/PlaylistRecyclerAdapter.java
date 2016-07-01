package ru.imunit.maquiz.views.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
        RecyclerView.Adapter<PlaylistRecyclerAdapter.PlaylistViewHolder> implements
        View.OnClickListener {

    private List<DBTrack> mDataset;
    private ItemClickListener mListener;

    public PlaylistRecyclerAdapter(List<DBTrack> dataset) {
        this.mDataset = dataset;
    }

    public void setOnClickListener(ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.recycler_item_2, parent, false);
//        ImageView iv = (ImageView)v.findViewById(R.id.icon);
//        iv.setImageResource(R.drawable.folder);
        v.setOnClickListener(this);
        return new PlaylistViewHolder(v);
    }

    @Override
    public void onClick(final View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenuInflater().inflate(R.menu.playlist_track_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_blacklist) {
                    if (mListener != null) {
                        int pos = (int)view.getTag(R.string.tag_item_pos);
                        mListener.onBlacklistTrack(mDataset.get(pos));
                    }
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int pos) {
        DBTrack track = mDataset.get(pos);
        holder.artist.setText(track.getArtist());
        holder.title.setText(track.getName());
        if (track.getIsBlacklisted() == 0)
            holder.icon.setImageResource(R.drawable.track);
        else
            holder.icon.setImageResource(R.drawable.library_music);
        // store the position as a tag to retrieve it from view in onClick handler
        holder.itemView.setTag(R.string.tag_item_pos, pos);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView artist;
        public TextView title;
        public ImageView icon;
        public PlaylistViewHolder(View itemView) {
            super(itemView);
            this.artist = (TextView)itemView.findViewById(R.id.firstLine);
            this.title = (TextView)itemView.findViewById(R.id.secondLine);
            this.icon = (ImageView)itemView.findViewById(R.id.icon);
        }
    }

    public interface ItemClickListener {
        void onBlacklistTrack(DBTrack track);
    }
}

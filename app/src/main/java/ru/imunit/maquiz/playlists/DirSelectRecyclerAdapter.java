package ru.imunit.maquiz.playlists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.imunit.maquiz.widgets.TriCheckBox;
import ru.imunit.maquiz.R;

/**
 * Created by imunit on 26.10.15.
 */
public class DirSelectRecyclerAdapter extends
        RecyclerView.Adapter<DirSelectRecyclerAdapter.ViewHolder> {

    List<AudioDirectory> mDataset;

    public DirSelectRecyclerAdapter(List<AudioDirectory> dataset) {
        mDataset = dataset;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView header;
        public TextView footer;
        public TriCheckBox state;
        public ViewHolder(View itemView) {
            super(itemView);
            header = (TextView)itemView.findViewById(R.id.firstLine);
            footer = (TextView)itemView.findViewById(R.id.secondLine);
            state = (TriCheckBox)itemView.findViewById(R.id.checkbox);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_checkable,
                parent, false);
        ImageView iv = (ImageView)v.findViewById(R.id.icon);
        iv.setImageResource(R.drawable.folder);
        return new ViewHolder(iv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        AudioDirectory ad = mDataset.get(pos);
        holder.header.setText(ad.getName());
        holder.footer.setText(String.format("Tracks: %d; Folders: %d",
                ad.getTracksCount(), ad.getDirsCount()));
        boolean enabled = ad.getTracksCount() > 0 && ad.getDirsCount() > 0;
        holder.state.setEnabled(enabled);
        if (enabled)
            holder.state.setState(ad.getState());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

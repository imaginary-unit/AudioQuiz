package ru.imunit.maquiz.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import ru.imunit.maquiz.R;


public class CheckRecyclerAdapter extends RecyclerView.Adapter<CheckRecyclerAdapter.ViewHolder>
 implements View.OnClickListener {

    private HashMap<String, Boolean> mDataset;
    private String[] mKeys;
    private ItemClickListener mListener;

    public CheckRecyclerAdapter(HashMap<String, Boolean> dataset) {
        this.mDataset = dataset;
        mKeys = dataset.keySet().toArray(new String[dataset.size()]);
    }

    public void setOnClickListener(ItemClickListener listener) {
        mListener = listener;
    }

    public void setDataset(HashMap<String, Boolean> dataset) {
        this.mDataset = dataset;
        mKeys = dataset.keySet().toArray(new String[dataset.size()]);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.recycler_item_check, parent, false);
        ImageView iv = (ImageView)v.findViewById(R.id.icon);
        iv.setImageResource(R.drawable.folder);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String key = mKeys[position];
        Boolean val = mDataset.get(key);
        holder.textContent.setText(key);
        holder.check.setChecked(val);
        // store the position as a tag to retrieve it from view in onClick handler
        holder.itemView.setTag(R.string.tag_item_pos, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            int pos = (int) view.getTag(R.string.tag_item_pos);
            String key = mKeys[pos];
            Boolean val = mDataset.get(mKeys[pos]);
            mListener.onClick(key, val);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textContent;
        public CheckBox check;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textContent = (TextView)itemView.findViewById(R.id.textContent);
            this.check= (CheckBox)itemView.findViewById(R.id.check);
        }
    }

    public interface ItemClickListener {
        void onClick(String dir, boolean state);
    }
}

package org.volkszaehler.volkszaehlerapp.adapter;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.volkszaehler.volkszaehlerapp.ChannelDetails;
import org.volkszaehler.volkszaehlerapp.MainActivity;
import org.volkszaehler.volkszaehlerapp.R;
import org.volkszaehler.volkszaehlerapp.generic.Channel;

import java.util.List;
import java.util.Locale;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Channel> items;
    private final MainActivity context;

    public CustomAdapter(List<Channel> items, MainActivity context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(v);
    }

    @Override

    public void onBindViewHolder(RecyclerView.ViewHolder holder_, int position) {
        CustomViewHolder holder = (CustomViewHolder) holder_;
        Channel currentItem = items.get(position);

        holder.channelName.setText(currentItem.getTitle());
        holder.channelDesc.setText(currentItem.getType().toString());
        holder.channelValue.setText(Float.toString(currentItem.getWert()));

        String col = (currentItem.getColor() == null || currentItem.getColor().isEmpty()) ? "#0000FF" : currentItem.getColor();

        if (col.startsWith("#")) {
            holder.channelName.setTextColor(Color.parseColor(col.toUpperCase(Locale.getDefault())));
            holder.channelValue.setTextColor(Color.parseColor(col.toUpperCase(Locale.getDefault())));
        }
        // Workarounds for non existing Colors on S4Mini 4.2.2
        else if (col.equals("teal")) {
            holder.channelName.setTextColor(context.getResources().getColor(R.color.teal));
            holder.channelValue.setTextColor(context.getResources().getColor(R.color.teal));
        } else if (col.equals("aqua")) {
            holder.channelName.setTextColor(context.getResources().getColor(R.color.aqua));
            holder.channelValue.setTextColor(context.getResources().getColor(R.color.aqua));
        } else {
            try {
                holder.channelName.setTextColor(Color.parseColor(col));
                holder.channelValue.setTextColor(Color.parseColor(col));
            } catch (IllegalArgumentException e) {
                Log.e("MainActivity", "Error setting color " + e.getMessage());
            }
        }
        holder.channelContainer.setOnClickListener(v -> {
            Intent in = new Intent(context.getApplicationContext(), ChannelDetails.class);
            in.putExtra("tuplesWert", currentItem.getUuid());
            in.putExtra("uuid", currentItem.getUuid());
            //in.putExtra("tuplesZeit", map.get("tuplesZeit"));

            context.startActivity(in);
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}


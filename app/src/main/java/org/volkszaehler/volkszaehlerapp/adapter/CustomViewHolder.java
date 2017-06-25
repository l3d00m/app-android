package org.volkszaehler.volkszaehlerapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.volkszaehler.volkszaehlerapp.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout channelContainer;
    public TextView channelName;
    public TextView channelDesc;
    public TextView channelValue;

    public CustomViewHolder(View itemView) {
        super(itemView);
        channelContainer = (RelativeLayout) itemView.findViewById(R.id.channelContainer);
        channelName = (TextView) itemView.findViewById(R.id.channelName);
        channelDesc = (TextView) itemView.findViewById(R.id.channelDescription);
        channelValue = (TextView) itemView.findViewById(R.id.channelValue);
    }

}

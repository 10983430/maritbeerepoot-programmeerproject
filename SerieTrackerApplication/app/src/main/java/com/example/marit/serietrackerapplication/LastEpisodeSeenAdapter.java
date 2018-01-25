package com.example.marit.serietrackerapplication;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marit on 24-1-2018.
 */

public class LastEpisodeSeenAdapter extends BaseAdapter {
    private final ArrayList mData;
    private HashMap<String, String> highestepisodeloggedin;

    public LastEpisodeSeenAdapter(HashMap<String, String> map, HashMap<String, String> highestepisodeloggedin) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
        this.highestepisodeloggedin = highestepisodeloggedin;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_episodes_seen, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        ((TextView) result.findViewById(R.id.Seriename)).setText(item.getKey());
        TextView highestep = result.findViewById(R.id.Highestep);
        highestep.setText(item.getValue());

        for (String key : highestepisodeloggedin.keySet()) {
            //Log.d("lolllzzzorrlol", key + " " + item.getKey());
            if (key == item.getKey()) {
                String[] partsuser = item.getValue().split("S");
                if (highestepisodeloggedin.get(key) != null) {
                    String[] partslog = highestepisodeloggedin.get(key).split("S");
                    String[] partsuserall = partsuser[1].split("E");
                    String[] partslogall = partslog[1].split("E");
                    if (Integer.parseInt(partslogall[0]) > Integer.parseInt(partsuserall[0])) {
                        highestep.setTextColor(Color.RED);
                    }
                    if (Integer.parseInt(partslogall[0]) == Integer.parseInt(partsuserall[0])) {
                        if (Integer.parseInt(partslogall[1]) > Integer.parseInt(partsuserall[1])) {
                            highestep.setTextColor(Color.RED);
                        }
                        if (Integer.parseInt(partslogall[1]) <= Integer.parseInt(partsuserall[1])) {
                            highestep.setTextColor(Color.GREEN);
                        }

                    }
                    else {
                        highestep.setTextColor(Color.GREEN);
                    }
                }

            }
        }

        return result;
    }

}

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
 * Creates an adapter that displays the episode name and the number & gives the number a color which
 * indicates if you can talk about the last episode you have seen without
 */
public class LastEpisodeSeenAdapter extends BaseAdapter {
    private final ArrayList mData;
    private HashMap<String, String> highestEpisodeLoggedIn;

    public LastEpisodeSeenAdapter(HashMap<String, String> map, HashMap<String,
            String> highestEpisodeLoggedIn) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
        this.highestEpisodeLoggedIn = highestEpisodeLoggedIn;
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
        //Inflate layout
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_layout_episodes_seen, parent, false);
        }
        // Set textviews
        Map.Entry<String, String> item = getItem(position);
        TextView seriename = convertView.findViewById(R.id.SerieName);
        seriename.setText(item.getKey());
        TextView highestEp = convertView.findViewById(R.id.HighestEp);
        highestEp.setText(item.getValue());

        // Set textview color
        colorPreparer(item, highestEp);
        return convertView;
    }

    /**
     * Prepares the data for the color setting
     */
    public void colorPreparer(Map.Entry<String, String> item, TextView highestEp) {
        for (String key : highestEpisodeLoggedIn.keySet()) {
            if (key == item.getKey()) {
                String[] splitHighestViewedUser = item.getValue().split("S");
                if (highestEpisodeLoggedIn.get(key) != null) {
                    // Split the Episode and Season number, season number is at position 0 and
                    // episode number is at position 1
                    String[] splitEpisodeLogged = highestEpisodeLoggedIn.get(key)
                            .split("S")[1].split("E");
                    String[] splitEpisodeViewed = splitHighestViewedUser[1].split("E");
                    colorTextView(splitEpisodeLogged, splitEpisodeViewed, highestEp);

                }
            }
        }
    }

    /**
     * Sets the color to red if the logged in user is further in the serie and green when the viewed
     * user and logged in user are at the same episode or the viewed user is further
     */
    public void colorTextView(String[] splitEpisodeLogged, String[] splitEpisodeViewed,
                              TextView highestep) {
        // If season of viewed user is lower, the logged in user could give him spoilers
        if (Integer.parseInt(splitEpisodeLogged[0]) > Integer.parseInt(splitEpisodeViewed[0])) {
            highestep.setTextColor(Color.RED);
        } else {

            // If the seasons are the same, but the episode of the viewed is lower,
            // the logged in user could give him spoilers
            if (Integer.parseInt(splitEpisodeLogged[0]) == Integer.parseInt(splitEpisodeViewed[0])) {
                if (Integer.parseInt(splitEpisodeLogged[1]) > Integer.parseInt(splitEpisodeViewed[1])) {
                    highestep.setTextColor(Color.RED);
                }

                // If the episode of the viewed in higher, the logged in user can not give
                // spoilers (but can receive them, that's not taken in to account in this app)
                if (Integer.parseInt(splitEpisodeLogged[1]) <= Integer.parseInt(splitEpisodeViewed[1])) {
                    highestep.setTextColor(Color.GREEN);
                }

            } else {
                highestep.setTextColor(Color.GREEN);
            }
        }
    }
}

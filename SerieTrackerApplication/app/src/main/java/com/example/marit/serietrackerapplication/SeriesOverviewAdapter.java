package com.example.marit.serietrackerapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Creates a custom list adapter that shows the name of a show and the image of the show
 */

public class SeriesOverviewAdapter extends ArrayAdapter<SearchResult> {
    public SeriesOverviewAdapter(Context context, ArrayList<SearchResult> results) {
        super(context, 0, results);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // Inflate the layout and get the data that belongs to the row
        SearchResult result = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, viewGroup, false);
        }

        // Set textview
        TextView titleView = view.findViewById(R.id.NameSerieView);
        titleView.setText(result.getTitle());

        // Set the imageview
        String url = result.getUrl();
        final ImageView imageview = view.findViewById(R.id.ImageSerieView);
        Picasso.with(getContext()).load(url).into(imageview);

        return view;
    }
}

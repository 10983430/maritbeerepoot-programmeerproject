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
 * Created by Marit on 11-1-2018.
 */

public class SeriesOverviewAdapter extends ArrayAdapter<SearchResult> {
    public SeriesOverviewAdapter(Context context, ArrayList<SearchResult> results) {
        super(context, 0, results);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        SearchResult result = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, viewGroup, false);
        }

        TextView titleView = view.findViewById(R.id.nameView);
        titleView.setText(result.getTitle());
        String url = result.getUrl();
        final ImageView imageview = view.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(url).into(imageview);

        return view;
    }
}

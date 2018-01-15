package com.example.marit.serietrackerapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Creates a custom listview adapter for an array with the usernames in it
 */

public class UsersOverviewAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> user;

    public UsersOverviewAdapter(Context context, ArrayList<String> listofusers) {
        super(context, 0, listofusers);
        this.context = context;
        this.user = listofusers;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        //Inflate the layout and set the views with information from the array
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row_layout_users, null);
        TextView username = view.findViewById(R.id.usernameHolder);
        username.setText(user.get(position));

        return view;
    }
}
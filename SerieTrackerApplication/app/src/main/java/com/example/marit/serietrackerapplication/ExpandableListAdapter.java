package com.example.marit.serietrackerapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Creates a custom expandable listview adapter that makes it possible to show the seasons as a
 * parent and the episodes as a child
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listData;
    private HashMap<String, List<Episode>> listHashMap;
    private String serieId;
    private ArrayList<String> seenEpisodes = new ArrayList<>();

    public ExpandableListAdapter(Context context, List<String> listData, HashMap<String, List<Episode>> listHashMap, String serieId) {
        this.context = context;
        this.listData = listData;
        this.listHashMap = listHashMap;
        this.serieId = serieId;
    }

    @Override
    public int getGroupCount() {
        return listData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listData.get(groupPosition);

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listData.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Get the season that should be in this position
        String title = (String) getGroup(groupPosition);

        // Inflate view if there is no view yet
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout_expandable_parent, null);
        }
        convertView.setFocusable(false);

        // Set the textview with the name of the season
        TextView viewtje = convertView.findViewById(R.id.usernameHolder);
        viewtje.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Inflate the layout
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_layout_expandable_child, null);
        convertView.setFocusable(false);

        // Get the episode that should be in this position
        Episode episode = (Episode) getChild(groupPosition, childPosition);

        //Set the textview with the title of the episode
        TextView viewtje = convertView.findViewById(R.id.EpisodeTitleView);
        viewtje.setText(episode.getTitle());

        // Find the episodes that are already seen and check the checkboxes of those
        String title = (String) getGroup(groupPosition);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            findSeenEpisodes(title, groupPosition, childPosition, convertView);
        }
        else {
            CheckBox checkBox = convertView.findViewById(R.id.CheckBox);
            checkBox.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Gets the information from the serie from the database and when an episode in in Firebase,
     * it checked the episode, because that means the episode is seen
     * All the variables are final, because they can't be changed during the process of setting
     * the checkboxes checked
     */
    public void findSeenEpisodes(final String season, final int groupPosition, final int childPosition, final View convertView) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + userid);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Gets the information of the serie and the specific season that
                // is expanded in the listview
                DataSnapshot value = dataSnapshot.child("SerieWatched").child(serieId).child(season);
                HashMap<String, String> episodes = (HashMap<String, String>) value.getValue();

                if (episodes == null) {
                    // If there are no episodes in Firebase it means that the user didn't watch
                    // an episode from the serie yet, so create an empty list
                    seenEpisodes = new ArrayList<>();
                } else {
                    // If there are entries for a specific season and serie in Firebase, get the
                    // episode, parse the string with regex and add it to the seenEpisodes list
                    seenEpisodes = new ArrayList<>();
                    for (String key : episodes.keySet()) {
                        String[] parts = key.split("-");
                        seenEpisodes.add(parts[1]);
                    }
                    // After the list for a season is made, check the boxes of
                    // the episodes that are in the list
                    Episode episode = (Episode) getChild(groupPosition, childPosition);
                    CheckBox checkBox = convertView.findViewById(R.id.CheckBox);
                    if (user != null) {
                        for (int i = 0; i < seenEpisodes.size(); i++) {
                            if (Integer.parseInt(seenEpisodes.get(i)) == episode.getEpisode()) {
                                checkBox.setChecked(true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // This error can only occur when there is an server-side reason to do so
                System.out.println("FIREBASE ERROR");
            }
        });

    }


}
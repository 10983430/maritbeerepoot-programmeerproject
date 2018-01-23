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
 * Created by Marit on 16-1-2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listData;
    private HashMap<String, List<Episode>> listHashMap;
    private DataSnapshot dataSnapshot;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String serieName;
    private boolean seen = false;
    private ArrayList<String> seenEpisodes = new ArrayList<>();

    public ExpandableListAdapter(Context context, List<String> listData, HashMap<String, List<Episode>> listHashMap, String serieName) {
        this.context = context;
        this.listData = listData;
        this.listHashMap = listHashMap;
        this.serieName = serieName;
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
        //seenEpisodes = new ArrayList<>();
        //seenEpisodes = findSeenEpisodes(title);

        // Inflate view if there is no view yet
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout_users, null);
        }

        convertView.setFocusable(false);
        // Set the textview with the name of the season
        TextView viewtje = convertView.findViewById(R.id.usernameHolder);
        viewtje.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Get the episode that should be in this position
        String title = (String) getGroup(groupPosition);
        seenEpisodes = findSeenEpisodes(title);
        Episode episode = (Episode) getChild(groupPosition, childPosition);

        Log.d("test20000000000000", title);

        Log.d("test20000000000000", seenEpisodes.toString());
        // Inflate view if there is no view yet

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_layout_expandable_child, null);

        convertView.setFocusable(false);
        //Set the textview with the title of the episode
        TextView viewtje = convertView.findViewById(R.id.EpisodeTitleView);
        viewtje.setText(episode.getTitle());
        Log.d("lolzzz", episode.getTitle()+" "+String.valueOf(childPosition));
        final CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        Boolean hi;
        //Log.d("testje32423324", episodes.toString());
        if (user != null) {
            //if (episode.getEpisode() == 5 || episode.getEpisode() == 9) {
            //hi = checkIfSeen(episode, groupPosition);
            //Log.d("hiii", String.valueOf(hi) + " " + episode.getEpisode());
            //checkBox.setChecked(hi);
            //Log.d("hiii", String.valueOf(hi) + " " + episode.getEpisode());
            //Log.d("hiii", String.valueOf(hi) + " " + episode.getEpisode());
            //checkBox.setChecked(hi);
            for (int i = 0; i < seenEpisodes.size(); i++) {
                Log.d("test2000", seenEpisodes.get(i) + " " + episode.getEpisode());
                Log.d("test2000", seenEpisodes.toString());


                if (Integer.parseInt(seenEpisodes.get(i)) == episode.getEpisode()) {
                    Log.d("test20000o", episode.getEpisode().toString());
                    checkBox.setChecked(true);
                    notifyDataSetChanged();
                }

            }
        }
            //seen = false;
        //}
        else {
            checkBox.setChecked(false);
        }
        return convertView;


    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean checkIfSeen(final Episode episode, final Integer position) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        String userid = user.getUid();
        DatabaseReference dbref = fbdb.getReference("User/"+userid);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot value = dataSnapshot.child("SerieWatched").child(serieName).child(listData.get(position));
                HashMap<String, String> episodes = (HashMap<String, String>) value.getValue();
                if (episodes == null) {
                    seen = false;
                }
                else {
                    for (String key : episodes.keySet()) {
                        String[] parts = key.split("-");
                        //Log.d("hiiiii", parts[1] + episode.getEpisode().toString());
                        Log.d("hiiiiiiiiiiiiiiii", episode.getEpisode().toString());
                        if (Integer.parseInt(parts[1]) == episode.getEpisode()) {
                            Log.d("hiiiii", "YES");
                            Log.d("hiiiii", parts[1] + episode.getEpisode().toString());
                            seen = true;
                            Log.d("hiiiii", String.valueOf(seen));
                            Log.d("lolzzz", String.valueOf(seen) + " " + episode.getEpisode());
                        } else {
                            seen = false;
                            Log.d("hiiiii", "NOPE");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return seen;
    }

    public ArrayList findSeenEpisodes(final String season) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        String userid = user.getUid();
        DatabaseReference dbref = fbdb.getReference("User/"+userid);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot value = dataSnapshot.child("SerieWatched").child(serieName).child(season);
                HashMap<String, String> episodes = (HashMap<String, String>) value.getValue();
                if (episodes == null) {
                    seenEpisodes = new ArrayList<>();
                }
                else {
                    Log.d("test2000000", episodes.toString());
                    seenEpisodes = new ArrayList<>();
                    for (String key : episodes.keySet()) {
                        String[] parts = key.split("-");
                        seenEpisodes.add(parts[1]);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return seenEpisodes;
    }
}

package com.example.marit.serietrackerapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;


/**
 * Displays the details about an user in de UI
 */
public class UserDetailsFragment extends ListFragment implements View.OnClickListener {
    private String userID;
    private HashMap<String, String> titles = new HashMap<>();
    private HashMap<String, String> lastEpisodeSeen = new HashMap<>();
    private HashMap<String, String> lastEpisodeSeenLoggedIn = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        // Set listeners
        Button follow = view.findViewById(R.id.FollowButton);
        Button unfollow = view.findViewById(R.id.UnfollowButton);
        ImageButton imageButton = view.findViewById(R.id.InfoButton);
        imageButton.setOnClickListener(this);
        follow.setOnClickListener(this);
        unfollow.setOnClickListener(this);
        updateUI(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        // Get the imdbid from the serie that was clicked on
        if (bundle != null) {
            userID = bundle.getString("userid");

            // Override the shared preferences, so that the episode clicked on before
            // is not in de shared preferences anymore
            SharedPreferences prefs = getContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
            SharedPreferences.Editor prefseditor = prefs.edit();
            prefseditor.putString("id", userID);
            prefseditor.apply();

            // Get the user data
            getUserData(userID);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the userID in Shared Preferences
        SharedPreferences prefs = getContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        SharedPreferences.Editor prefseditor = prefs.edit();
        prefseditor.putString("id", userID);
        prefseditor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        String id = prefs.getString("id", "Default");
        if (!id.equals("Default")) {
            getUserData(id);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.FollowButton:
                // Follow the user by putting their information in Firebase
                putUserInDatabase();
                updateUI(getView());
                break;

            case R.id.UnfollowButton:
                // Unfollow the user by deleting their information in Firebase
                unfollow();
                updateUI(getView());
                break;

            case R.id.InfoButton:
                FragmentManager fm = getFragmentManager();
                ColorInformationDialogFragment dialogFragment = new ColorInformationDialogFragment();
                dialogFragment.show(fm, "Explanation colors");
        }
    }

    /**
     * Sets the visibility of the follow and unfollow button, according to if a user is already
     * following the viewed user
     */
    public void updateUI(final View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentUser = user.getUid();
            // Make it impossible to follow yourself
            if (userID.equals(currentUser)) {
                Button follow = view.findViewById(R.id.FollowButton);
                follow.setVisibility(GONE);
            }
            // Check if the button should be for following or unfollowing
            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            String userid = user.getUid();
            DatabaseReference dbref = fbdb.getReference("User/" + userid);
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot usersFollowed = dataSnapshot.child("UsersFollowed").child(userID);
                    // If the datasnapshot is not null, this means that the user is already in
                    // Firebase and thereby followed so show unfollow button
                    if (usersFollowed.getValue() != null) {
                        (view.findViewById(R.id.UnfollowButton)).setVisibility(View.VISIBLE);
                        (view.findViewById(R.id.FollowButton)).setVisibility(GONE);
                    }
                    else {
                        (view.findViewById(R.id.UnfollowButton)).setVisibility(View.GONE);
                        (view.findViewById(R.id.FollowButton)).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TODO handelen
                }
            });
        } else {
            // Hide the follow button when there is no user logged in
            Button follow = view.findViewById(R.id.FollowButton);
            follow.setVisibility(GONE);
        }
    }

    /**
     * 'Follows" the user by putting the user in the database
     */
    public void putUserInDatabase() {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        final DatabaseReference dbref = fbdb.getReference("User/" + currentUserId + "/UsersFollowed");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> usersFollowed = (HashMap<String, String>) dataSnapshot.getValue();
                if (usersFollowed == null) {
                    usersFollowed = new HashMap<>();
                }
                TextView view = getView().findViewById(R.id.UsernameInfo);
                String username = view.getText().toString();
                usersFollowed.put(userID, username);
                try {
                    dbref.setValue(usersFollowed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//TODO
            }
        });
    }

    /**
     * Unfollows the user by removing him from the database
     */
    public void unfollow() {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference dbref = fbdb.getReference("User/" + userId + "/UsersFollowed/" + userID);
        dbref.removeValue();
    }

    /**
     * Gets the last seen episode of every serie from the viewed user
     */
    public void getUserData(final String userID) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + userID);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue().toString();
                TextView usernamehold = getView().findViewById(R.id.UsernameInfo);
                usernamehold.setText(username);
                HashMap<String, HashMap<String, HashMap<String, String>>> info =
                        (HashMap<String, HashMap<String, HashMap<String, String>>>) dataSnapshot.child("SerieWatched").getValue();
                if (info == null) {
                    (getView().findViewById(R.id.NoneWatched)).setVisibility(View.VISIBLE);
                    (getView().findViewById(R.id.WatchedSeries)).setVisibility(View.GONE);
                } else {
                    // Get the data from the logged in user to compare later
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        getLoggedInUserData(user.getUid());
                    }
                    lastEpisodeSeen = searchHighestEpisode(info, dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO handelen
            }
        });
    }

    /**
     * Puts the name of the serie and the last episode that was seen from a serie in a hashmap
     */
    public HashMap<String, String> searchHighestEpisode(HashMap<String, HashMap<String, HashMap<String, String>>> info, DataSnapshot dataSnapshot) {
        HashMap<String, String> hashMap = new HashMap<>();
        // Loop through all the series
        for (String serieId : info.keySet()) {
            getSerieData(serieId, 1);
            DataSnapshot serieInfoDataSnapshot = dataSnapshot.child("SerieWatched").child(serieId);
            HashMap<String, HashMap<String, String>> serieinfo = (HashMap<String, HashMap<String, String>>) serieInfoDataSnapshot.getValue();

            // Add all the season numbers from the serie to an arraylist
            ArrayList<Integer> seasons = new ArrayList<>();
            for (String seasonName : serieinfo.keySet()) {
                String[] parts = seasonName.split(" ");
                seasons.add(Integer.parseInt(parts[1]));
            }

            // Get the highest season by getting the max value from the array list
            String highestSeason = String.valueOf(Collections.max(seasons));

            // Get the data from the highest season
            DataSnapshot episodeInfoDataSnapshot = dataSnapshot.child("SerieWatched").child(serieId).child("Season " + highestSeason);
            HashMap<String, String> episodeinfo = (HashMap<String, String>) episodeInfoDataSnapshot.getValue();

            // Add all the episodenumbers from the highest season to an arraylist
            ArrayList<Integer> episodes = new ArrayList<>();
            for (String episode : episodeinfo.keySet()) {
                String[] parts = episode.split("-");
                episodes.add(Integer.parseInt(parts[1]));
            }
            // Get the highest episode by getting the max value from the array list
            String highestEpisode = String.valueOf(Collections.max(episodes));

            // Put the highest season in a hashmap in the format S[seasonnumber]E[episodenumber]
            hashMap.put(serieId, "S" + highestSeason + "E" + highestEpisode);
        }
        return hashMap;
    }

    /**
     * Gets the last seen episode of every serie from the logged in user
     */
    public void getLoggedInUserData(String userID) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + userID);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, HashMap<String, String>>> info =
                        (HashMap<String, HashMap<String, HashMap<String, String>>>) dataSnapshot.child("SerieWatched").getValue();
                if (info == null) {
                    lastEpisodeSeenLoggedIn = new HashMap<>();
                } else {
                    lastEpisodeSeenLoggedIn = searchHighestEpisode(info, dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//TODO
            }
        });
    }

    /**
     * Requests the data for imdbid, so the title can be parsed to create a listview with the
     * title and the last episode from a serie someone saw
     */
    public void getSerieData(final String key, final int parameter) {
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + key;
        // Create new queue
        RequestQueue RequestQueue = Volley.newRequestQueue(getContext());
        // Create new stringrequest (Volley)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String reaction) {
                        try {
                            if (parameter == 1) {
                                // Parse JSON to a object and make set adapter
                                getSerieTitle(reaction.toString(), key);
                                makeListView();
                            } else {
                                getSerieTitle(reaction.toString(), key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue.add(stringRequest);
    }

    /**
     * Parses the title of a serie from the JSON and puts it in a Hashmap<Title, imdbid>
     */
    public void getSerieTitle(String response, String imdbid) throws JSONException {
        try {
            JSONObject data = new JSONObject(response);
            String title = data.getString("Title");
            titles.put(imdbid, title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a listview by merging the hashmaps with the highestepisodes and the name
     */
    public void makeListView() {
        HashMap<String, String> highestepontitle = new HashMap<>();
        HashMap<String, String> seenepisodes = new HashMap<>();
        // Make sure that all the API data is parsed by checking if the titles hashmap is equal in
        // size or bigger than the highestepisodes hashmap from the viewed user
        if (titles.size() >= lastEpisodeSeen.size()) {
            for (String imdbid : lastEpisodeSeen.keySet()) {
                // Create hashMaps that have the title of the serie instead of the imdbid
                highestepontitle.put(titles.get(imdbid), lastEpisodeSeen.get(imdbid));
                seenepisodes.put(titles.get(imdbid), lastEpisodeSeenLoggedIn.get(imdbid));
            }
            ListAdapter adapter = new LastEpisodeSeenAdapter(highestepontitle, seenepisodes);
            getListView().setAdapter(adapter);
            getListView().invalidateViews();

        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Get the imdbid from the clicked serie
        TextView view = v.findViewById(R.id.SerieName);
        String title = view.getText().toString();
        String imdbid = new String();
        for (String key : titles.keySet()) {
            if (titles.get(key).equals(title)) {
                imdbid = key;
            }
        }

        // Navigate to the serie details
        SerieDetailsFragment fragment = new SerieDetailsFragment();
        Bundle args = new Bundle();
        args.putString("imdbid", imdbid);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }
}





package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends ListFragment implements View.OnClickListener {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userID;

    private DatabaseReference dbref;
    String username;
    HashMap<String, String> titles = new HashMap<>();
    HashMap<String, String> highestepisode = new HashMap<>();
    HashMap<String, String> highestepisodeloggedin = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        Button follow = view.findViewById(R.id.FollowButton);
        Button unfollow = view.findViewById(R.id.UnfollowButton);
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
            Log.d("lollollll", userID);
            getUserData(userID);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.FollowButton:
                putUserInDatabase();
                // TO-DO bug fixen dat hij niet spaced bij het updaten
                //updateUI(view);
                break;
            case R.id.UnfollowButton:
                Log.d("lolllzzzo", "test2");
                FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
                String userid = user.getUid();
                DatabaseReference dbref = fbdb.getReference("User/" + userid + "/UsersFollowed/" + userID);
                Log.d("lolllzzzo", "test3");
                dbref.removeValue();
                Log.d("lolllzzzo", "test4");
                //updateUI(view);
                break;
        }
    }

    /**
     * 'Follows" the user by putting the user in the database
     */
    public void putUserInDatabase() {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        String currentuserid = user.getUid();
        dbref = fbdb.getReference("User/" + currentuserid + "/UsersFollowed");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> usersfollowed = (HashMap<String, String>) dataSnapshot.getValue();
                if (usersfollowed == null) {
                    usersfollowed = new HashMap<>();
                }
                usersfollowed.put(userID, username);
                try {
                    dbref.setValue(usersfollowed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // TO-DO deze functie korter maken lol

    /**
     * Gets all the watched series a user watched from Firebase and finds the last episode that
     * was seen
     */
    public void getUserData(final String userID) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + userID);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue().toString();
                TextView usernamehold = getView().findViewById(R.id.UsernameInfo);
                usernamehold.setText(username);
                HashMap<String, HashMap<String, HashMap<String, String>>> info =
                        (HashMap<String, HashMap<String, HashMap<String, String>>>) dataSnapshot.child("SerieWatched").getValue();
                if (info == null) {
                    TextView nonewatched = getView().findViewById(R.id.Nonewatched);
                    nonewatched.setVisibility(View.VISIBLE);
                } else {
                    getLoggedInUserData(user.getUid());
                    Log.d("lollol", "hiii");
                    Log.d("lollol", info.keySet().toString());
                    //ArrayList<String> keyset = (ArrayList<String>) info.keySet();
                    for (String key : info.keySet()) {
                        getSerieData(key, 1);
                        // TO-DO hier opdelen?
                        DataSnapshot serieinfodatasnapshot = dataSnapshot.child("SerieWatched").child(key);
                        HashMap<String, HashMap<String, String>> serieinfo = (HashMap<String, HashMap<String, String>>) serieinfodatasnapshot.getValue();
                        ArrayList<Integer> seasons = new ArrayList<>();
                        for (String key2 : serieinfo.keySet()) {
                            String[] parts = key2.split(" ");
                            seasons.add(Integer.parseInt(parts[1]));
                        }
                        String highestseason = String.valueOf(Collections.max(seasons));
                        DataSnapshot episodeinfodatasnapshot = dataSnapshot.child("SerieWatched").child(key).child("Season " + highestseason);
                        HashMap<String, String> episodeinfo = (HashMap<String, String>) episodeinfodatasnapshot.getValue();
                        ArrayList<Integer> episodes = new ArrayList<>();
                        for (String key3 : episodeinfo.keySet()) {
                            String[] parts = key3.split("-");
                            episodes.add(Integer.parseInt(parts[1]));
                        }
                        String highestEpisode = String.valueOf(Collections.max(episodes));

                        highestepisode.put(key, "S" + highestseason + "E" + highestEpisode);

                    }
                    Log.d("lollol", highestepisode.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getLoggedInUserData(String userID) {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference("User/" + userID);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, HashMap<String, String>>> info =
                        (HashMap<String, HashMap<String, HashMap<String, String>>>) dataSnapshot.child("SerieWatched").getValue();
                if (info == null) {
                    highestepisodeloggedin = new HashMap<>();
                }
                else {
                    for (String key : info.keySet()) {
                        //getSerieData(key, 2);
                        // TO-DO hier opdelen?
                        DataSnapshot serieinfodatasnapshot = dataSnapshot.child("SerieWatched").child(key);
                        HashMap<String, HashMap<String, String>> serieinfo = (HashMap<String, HashMap<String, String>>) serieinfodatasnapshot.getValue();
                        ArrayList<Integer> seasons = new ArrayList<>();
                        for (String key2 : serieinfo.keySet()) {
                            String[] parts = key2.split(" ");
                            seasons.add(Integer.parseInt(parts[1]));
                        }
                        String highestseason = String.valueOf(Collections.max(seasons));
                        DataSnapshot episodeinfodatasnapshot = dataSnapshot.child("SerieWatched").child(key).child("Season " + highestseason);
                        HashMap<String, String> episodeinfo = (HashMap<String, String>) episodeinfodatasnapshot.getValue();
                        ArrayList<Integer> episodes = new ArrayList<>();
                        for (String key3 : episodeinfo.keySet()) {
                            String[] parts = key3.split("-");
                            episodes.add(Integer.parseInt(parts[1]));
                        }
                        String highestEpisode = String.valueOf(Collections.max(episodes));
                        Log.d("lolllzzzorr", highestEpisode);

                        highestepisodeloggedin.put(key, "S" + highestseason + "E" + highestEpisode);
                    }
                    Log.d("lolllzzzorr", highestepisodeloggedin.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                                Log.d("oooooo", key);
                                parseJSON(reaction.toString(), key);
                                Log.d("lollol", titles.toString());

                                makeListView();
                            }
                            else {
                                parseJSON(reaction.toString(), key);
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
    public void parseJSON(String response, String imdbid) throws JSONException {
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
        if (titles.size() == highestepisode.size()) {
            for (String imdbid : titles.keySet()) {
                highestepontitle.put(titles.get(imdbid), highestepisode.get(imdbid));
                seenepisodes.put(titles.get(imdbid), highestepisodeloggedin.get(imdbid));
            }
            Log.d("lolllzzzorrloll2", seenepisodes.toString());
            ListAdapter adapter = new LastEpisodeSeenAdapter(highestepontitle, seenepisodes);
            getListView().setAdapter(adapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView view = v.findViewById(R.id.Seriename);
        String hoi = view.getText().toString();
        Log.d("lolzzzz", hoi);
        String imdbid = new String();
        for (String key : titles.keySet()) {
            if (titles.get(key) == hoi) {
                imdbid = key;
            }
        }
        SerieDetailsFragment fragment = new SerieDetailsFragment();
        Bundle args = new Bundle();
        args.putString("imdbid", imdbid);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    public void updateUI(final View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentuser = user.getUid();
            // Make it impossible to follow yourself
            if (userID.equals(currentuser)) {
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
                    DataSnapshot dataSnapshot1 = dataSnapshot.child("UsersFollowed").child(userID);

                    Log.d("lolzzz", dataSnapshot1.toString());
                    if (dataSnapshot1.getValue() != null) {
                        String user = dataSnapshot1.getValue().toString();
                        Button unfollow = view.findViewById(R.id.UnfollowButton);
                        unfollow.setVisibility(View.VISIBLE);
                        Button follow = view.findViewById(R.id.FollowButton);
                        follow.setVisibility(GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}



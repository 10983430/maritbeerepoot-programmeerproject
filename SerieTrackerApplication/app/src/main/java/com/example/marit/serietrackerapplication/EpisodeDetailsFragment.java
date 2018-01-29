package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class EpisodeDetailsFragment extends Fragment implements View.OnClickListener {
    private String imdbid;
    private String imdbidserie;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase fbdb;
    private DatabaseReference dbref;
    private String episode;
    private String seasonnumber;
    private String episodetitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_episode_details, container, false);
        Button search = view.findViewById(R.id.FirebaseButton);

        // Set a listener on the search button to make it operational
        search.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        // Get the imdbid from the serie that was clicked on
        if (bundle != null) {
            imdbidserie = bundle.getString("imdbidserie");
            imdbid = bundle.getString("imdbid");
        }
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
        getData(url);
    }


    public void getData(String url) {
        // Create new queue
        RequestQueue RQ = Volley.newRequestQueue(getContext());
        // Create new stringrequest (Volley)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse JSON to a object and make set adapter
                            parseEpisodeJSON(response.toString());
                            getFollowInfo();

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
        RQ.add(stringRequest);
    }

    public void parseEpisodeJSON(String response) {
        try {
            JSONObject data = new JSONObject(response);

            TextView name = getView().findViewById(R.id.EpisodeNameInfo);
            episodetitle = data.getString("Title");
            name.setText(episodetitle);

            TextView season = getView().findViewById(R.id.EpisodeSeasonInfo);
            seasonnumber = data.getString("Season");
            season.setText(seasonnumber);

            TextView episodenumber = getView().findViewById(R.id.EpisodeNumberInfo);
            episode = data.getString("Episode");
            episodenumber.setText(episode);

            TextView releasedate = getView().findViewById(R.id.EpisodeReleaseInfo);
            releasedate.setText(data.getString("Released"));

            TextView imdbrating = getView().findViewById(R.id.EpisodeRatingInfo);
            imdbrating.setText(data.getString("imdbRating"));

            TextView genre = getView().findViewById(R.id.EpisodeGenreInfo);
            genre.setText(data.getString("Genre"));

            TextView director = getView().findViewById(R.id.EpisodeDirectorInfo);
            director.setText(data.getString("Director"));

            TextView writer = getView().findViewById(R.id.EpisodeWriterInfo);
            writer.setText(data.getString("Writer"));

            TextView plot = getView().findViewById(R.id.EpisodePlotInfo);
            plot.setText(data.getString("Plot"));

            TextView language = getView().findViewById(R.id.EpisodeLanguageInfo);
            language.setText(data.getString("Language"));

            final ImageView imageview = getView().findViewById(R.id.EpisodePoster);
            // Hide the imageview if there is no image available
            if (data.getString("Poster").equals("N/A")) {
                imageview.setVisibility(View.GONE);
            }
            else {
                Picasso.with(getContext()).load(data.getString("Poster")).into(imageview);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getFollowInfo() {
        if (user != null) {
            fbdb = FirebaseDatabase.getInstance();
            final String userid = user.getUid();
            dbref = fbdb.getReference("User/");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot snapshot = dataSnapshot.child(userid).child("UsersFollowed");
                    HashMap<String, String> followed = (HashMap<String, String>) snapshot.getValue();
                    if (followed != null) {
                        for (String key : followed.keySet()) {
                            Log.d("lolllzzzo", key);
                            DataSnapshot lol2 = dataSnapshot.child(key).child("username");
                            String username = lol2.getValue().toString();
                            DataSnapshot lol23 = dataSnapshot.child(key).child("SerieWatched").child(imdbidserie).child("Season " + seasonnumber).child("E-" + episode);

                            if (lol23.getValue() == null) {
                                TextView lol = getView().findViewById(R.id.FollowersInfo);
                                lol.setText(lol.getText().toString() + username + " didn't watch this episode yet!\n");
                            } else {
                                TextView lol = getView().findViewById(R.id.FollowersInfo);
                                lol.setText(lol.getText().toString() + username + " did watch this episode!\n");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FirebaseButton:
                Log.d("proooooo", "hooooo");
                if (user != null) {
                    fbdb = FirebaseDatabase.getInstance();
                    String userid = user.getUid();
                    dbref = fbdb.getReference("User/" + userid);

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot value = dataSnapshot.child("SerieWatched");
                            HashMap<String, HashMap<String, HashMap<String, String>>> seen = (HashMap<String, HashMap<String, HashMap<String, String>>>) value.getValue();

                            // If this is the first episode ever added to firebase, create a new
                            // new hashmap for all the episodes
                            if (seen == null) {
                                seen = new HashMap<>();
                                HashMap<String, HashMap<String, String>> season = new HashMap<>();
                                HashMap<String, String> episodeHashmap = new HashMap<>();
                                episodeHashmap.put("E-" + episode, episodetitle);
                                season.put("Season " + seasonnumber, episodeHashmap);
                                seen.put(imdbidserie, season);
                            } else {
                                // Check if there is an episode of the serie that needs to be added
                                // in the database, by checking if there is a key with the serie title
                                DataSnapshot serietitle = dataSnapshot.child("SerieWatched").child(imdbidserie);
                                HashMap<String, HashMap<String, String>> seriefb = (HashMap<String, HashMap<String, String>>) serietitle.getValue();

                                // If not, create a new hashmap for the serie with the episode and season in it
                                if (seriefb == null) {
                                    HashMap<String, HashMap<String, String>> season = new HashMap<>();
                                    HashMap<String, String> episodeHashmap = new HashMap<>();
                                    episodeHashmap.put("E-" + episode, episodetitle);
                                    season.put("Season " + seasonnumber, episodeHashmap);
                                    seen.put(imdbidserie, season);
                                } else {
                                    // If there is a episode from a specific serie in the database,
                                    // check if there is already an episode added from the season
                                    DataSnapshot seasontje = dataSnapshot.child("SerieWatched").child(imdbidserie).child("Season " + seasonnumber);
                                    HashMap<String, String> episodeHashmap = (HashMap<String, String>) seasontje.getValue();

                                    // If there isn't, add the season and the episode to the hashmap
                                    // with watched episodes
                                    if (episodeHashmap == null) {
                                        episodeHashmap = new HashMap<>();
                                        episodeHashmap.put("E-" + episode, episodetitle);
                                        seriefb.put("Season " + seasonnumber, episodeHashmap);
                                        seen.put(imdbidserie, seriefb);
                                    }

                                    // If there is, add the episode to the hasmap of the season and
                                    // to the hashmap with watched episodes
                                    else {
                                        episodeHashmap.put("E-" + episode, episodetitle);
                                        seriefb.put("Season " + seasonnumber, episodeHashmap);
                                        seen.put(imdbidserie, seriefb);
                                    }
                                }
                            }

                            // Update the database by inserting the hashmap with watched episodes
                            try {
                                dbref.child("SerieWatched").setValue(seen);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

        }
    }

}

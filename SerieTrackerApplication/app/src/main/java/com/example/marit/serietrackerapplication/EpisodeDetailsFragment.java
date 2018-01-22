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
    private String title;
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
            title = bundle.getString("title");
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
            Picasso.with(getContext()).load(data.getString("Poster")).into(imageview);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FirebaseButton:
                Log.d("proooooo", "hooooo");
                if (user != null){
                    fbdb = FirebaseDatabase.getInstance();
                    String userid = user.getUid();
                    dbref = fbdb.getReference("User/"+userid);

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot value = dataSnapshot.child("SerieWatched");
                            HashMap<String, HashMap<String, HashMap<String, String>>> seen = (HashMap<String, HashMap<String, HashMap<String, String>>>) value.getValue();

                            // If this is the first episode ever added to firebase, create a new hashmap
                            if (seen == null) {
                                seen = new HashMap<>();
                                HashMap<String, HashMap<String, String>> season = new HashMap<>();
                                HashMap<String, String> episodeHashmap = new HashMap<>();
                                episodeHashmap.put("E-" + episode, episodetitle);
                                season.put("S-" + seasonnumber, episodeHashmap);
                                seen.put(title, season);
                            }
                            else {
                                DataSnapshot serietitle = dataSnapshot.child("SerieWatched").child(title);
                                //Log.d("loooooooo", serietitle.toString());
                                //Log.d("testnameloooooo", serietitle.getValue().getClass().toString());

                                HashMap<String, HashMap<String, String>> seriefb = (HashMap<String, HashMap<String, String>>) serietitle.getValue();
                                //Log.d("loooooooo", seriefb.toString());

                                // Check if there is an episode of the serie that needs to be added
                                // in the database
                                if (seriefb == null) {
                                    HashMap<String, HashMap<String, String>> season = new HashMap<>();
                                    HashMap<String, String> episodeHashmap = new HashMap<>();
                                    episodeHashmap.put("E-" + episode, episodetitle);
                                    season.put("S-" + seasonnumber, episodeHashmap);
                                    seen.put(title, season);
                                }

                                else {
                                    // Check if there was already an episode from the season added
                                    DataSnapshot seasontje = dataSnapshot.child("SerieWatched").child(title).child("S-" + seasonnumber);
                                    HashMap<String, String> seasonsadded = (HashMap<String, String>) seasontje.getValue();
                                    if (seasontje == null) {
                                        seasonsadded.put("E-" + episode, episodetitle);
                                        seriefb.put("S-" + seasonnumber, seasonsadded);
                                        seen.put(title, seriefb);
                                    }
                                }
                            }
                            //HashMap<String, String> probeersel = new HashMap<>();
                            //probeersel.put(episode, title);

                            try{

                                dbref.child("SerieWatched").setValue(seen);
                            }
                            catch (Exception e){
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

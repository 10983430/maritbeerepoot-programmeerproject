package com.example.marit.serietrackerapplication;


import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


/**
 * Displays the details about an episode in de UI
 */
public class EpisodeDetailsFragment extends Fragment implements View.OnClickListener {
    private String imdbid;
    private String imdbidSerie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_episode_details, container, false);
        Button markAsSeen = view.findViewById(R.id.SeenButton);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Hide the "I've seen this" button when there is no user logged in
        if (user == null) {
            markAsSeen.setVisibility(View.GONE);
        }
        // Set a listener on the search button to make it operational
        markAsSeen.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the imdbid from the serie that was clicked on from the bundle
        Bundle imdbidbundle = this.getArguments();
        if (imdbidbundle != null) {
            imdbidSerie = imdbidbundle.getString("imdbidserie");
            imdbid = imdbidbundle.getString("imdbid");
            // Override the shared preferences, so that the episode clicked on before is not in de
            // shared preferences anymore
            SharedPreferences prefs = getContext().getSharedPreferences("EpisodeDetails", MODE_PRIVATE);
            SharedPreferences.Editor prefseditor = prefs.edit();
            prefseditor.putString("id", imdbid);
            prefseditor.commit();

            String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
            getEpisodeData(url);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getContext().getSharedPreferences("EpisodeDetails", MODE_PRIVATE);
        SharedPreferences.Editor prefseditor = prefs.edit();
        prefseditor.putString("id", imdbid);
        prefseditor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getContext().getSharedPreferences("EpisodeDetails", MODE_PRIVATE);
        String id = prefs.getString("id", "Default");
        //TODO if not van maken
        if (!id.equals("Default")) {
            String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + id;
            getEpisodeData(url);
        }
    }

    /**
     * Gets the episode data from the api
     */
    public void getEpisodeData(String url) {
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
                            String seasonNumber = ((TextView)
                                    getView().findViewById(R.id.EpisodeSeasonInfo)).getText().toString();
                            String episodeNumber = ((TextView)
                                    getView().findViewById(R.id.EpisodeNumberInfo)).getText().toString();
                            getFollowInfo(seasonNumber, episodeNumber);
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

    /**
     * Parses the JSON and puts the information into the textviews
     */
    public void parseEpisodeJSON(String response) {
        try {
            JSONObject data = new JSONObject(response);
            ((TextView) getView().findViewById(R.id.EpisodeNameInfo)).setText(data.getString("Title"));
            ((TextView) getView().findViewById(R.id.EpisodeSeasonInfo)).setText(data.getString("Season"));
            ((TextView) getView().findViewById(R.id.EpisodeNumberInfo)).setText(data.getString("Episode"));
            ((TextView) getView().findViewById(R.id.EpisodeReleaseInfo)).setText(data.getString("Released"));
            ((TextView) getView().findViewById(R.id.EpisodeRatingInfo)).setText(data.getString("imdbRating"));
            ((TextView) getView().findViewById(R.id.EpisodeGenreInfo)).setText(data.getString("Genre"));
            ((TextView) getView().findViewById(R.id.EpisodeDirectorInfo)).setText(data.getString("Director"));
            ((TextView) getView().findViewById(R.id.EpisodeWriterInfo)).setText(data.getString("Writer"));
            ((TextView) getView().findViewById(R.id.EpisodePlotInfo)).setText(data.getString("Plot"));
            ((TextView) getView().findViewById(R.id.EpisodeLanguageInfo)).setText(data.getString("Language"));
            // Hide the imageview if there is no image available
            final ImageView imageview = getView().findViewById(R.id.EpisodePoster);
            if (data.getString("Poster").equals("N/A")) {
                imageview.setVisibility(View.GONE);
            } else {
                Picasso.with(getContext()).load(data.getString("Poster")).into(imageview);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets information about if a user that is followed has already seen this episode from Firebase
     */
    public void getFollowInfo(final String seasonNumber, final String EpisodeNumber) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String userid = user.getUid();
            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            DatabaseReference dbref = fbdb.getReference("User/");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Make sure the textview is empty (for when onResume is called)
                    TextView followersInfo = getView().findViewById(R.id.FollowersInfo);
                    followersInfo.setText("");
                    // Get all the users that the logged in user is following
                    HashMap<String, String> followed = (HashMap<String, String>) dataSnapshot.child(userid).child("UsersFollowed").getValue();
                    if (followed != null) {
                        for (String key : followed.keySet()) {
                            String username = dataSnapshot.child(key).child("username").getValue().toString();
                            // Check if the episode is in the Firebase

                            DataSnapshot episodeInfo = dataSnapshot.child(key).child("SerieWatched").child(imdbidSerie).child("Season " + seasonNumber).child("E-" + episodeNumber);
                            if (episodeInfo.getValue() == null) {
                                followersInfo.setText(followersInfo.getText().toString() + username + " didn't watch this episode yet!\n");
                            } else {
                                followersInfo.setText(followersInfo.getText().toString() + username + " did watch this episode!\n");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
// TODO hier wat
                }
            });
        }
    }

    //TODO of deze switch aanpassen want maar 1 case, of nog een unseen button maken door te checken of item al in Firebase zit
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SeenButton:
                String episodeTitle = ((TextView)
                        getView().findViewById(R.id.EpisodeNameInfo)).getText().toString();
                String episodeNumber = ((TextView)
                        getView().findViewById(R.id.EpisodeNumberInfo)).getText().toString();
                String seasonNumber = ((TextView)
                        getView().findViewById(R.id.EpisodeSeasonInfo)).getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FireBaseHelper.markAsSeen(episodeTitle, seasonNumber, episodeNumber, user, imdbid);
        }
    }


}

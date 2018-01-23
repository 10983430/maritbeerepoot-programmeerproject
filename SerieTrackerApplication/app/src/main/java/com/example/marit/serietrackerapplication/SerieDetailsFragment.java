package com.example.marit.serietrackerapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SerieDetailsFragment extends Fragment implements View.OnClickListener {
    String imdbid;
    private ArrayList<Episode> episodeitems = new ArrayList<Episode>();
    private ExpandableListAdapter adapter;
    private ExpandableListView listview;
    private List<String> SeasonList;
    private HashMap<String, List<Episode>> hashMap;
    Integer totalseasons;
    Integer count = 0;
    Serie serieinfo;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DataSnapshot watched;
    String serieName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serie_details, container, false);
        Button search = view.findViewById(R.id.episodedetails);

        // Set a listener on the search button to make it operational
        search.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SeasonList = new ArrayList<>();
        hashMap = new HashMap<>();
        Bundle bundle = this.getArguments();
        // Get the imdbid from the serie that was clicked on
        if (bundle != null) {
            imdbid = bundle.getString("imdbid");
        }

        // Get the data from the clicked serie
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
        getData(url, 1, 0);
    }
/*
    @Override
    public void onResume() {
        //OnSaveState?
        String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid;
        getData(url, 1, 0);
        super.onResume();
    }*/

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.episodedetails:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                EpisodeDetailsFragment frag = new EpisodeDetailsFragment();
                fragmentTransaction.replace(R.id.fragment_container, frag).addToBackStack(null).commit();

        }
    }


    /**
     * Sends an volley request to get the JSON response from the api
     * @param url url that leads to the API
     * @param type 1 for the serie information, 2 for the season information
     * @param seasonnumber indicates the season number, 0 when general serie information is requested
     */
    public void getData(String url, Integer type, final Integer seasonnumber) {
        // Create new queue
        RequestQueue RQe = Volley.newRequestQueue(getContext());
        final Integer type2 = type;
        // Create new stringrequest (Volley)
        StringRequest stringRequeset = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (type2 == 1) {

                                Log.d("dsfsuewirewpirpwe", "hoiii1111");
                                parseJSONSerieDetails(response);
                            }
                            else {
                                count += 1;
                                Log.d("dsfsuewirewpirpwe", "hoiii");
                                parseJSONSeasons(response, seasonnumber);
                                Log.d("dsfsdfsdfsd", String.valueOf(episodeitems.size()));
                                fixData();
                                fillTextviews();

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
        RQe.add(stringRequeset);
    }

    public void checkboxClick(android.view.View sender) {
        CheckBox box = (CheckBox) sender;
        Log.d("CLICKJE", "lol");
    }

    private void fixData() {
        if (count == totalseasons) {
            for (int i = 1; i <= serieinfo.getTotalSeasons(); i++) {
                ArrayList<Episode> listje = new ArrayList<>();
                for (int x = 0; x < episodeitems.size(); x++) {
                    if (episodeitems.get(x).getSeasonnumber() == i) {

                        listje.add(episodeitems.get(x));
                    }
                }
                hashMap.put("Season " + i, listje);
            }
        }


        adapter =  new ExpandableListAdapter(getContext(), SeasonList, hashMap, serieName);
        ExpandableListView view = getView().findViewById(R.id.ExpandableListview);
        view.setAdapter(adapter);

        // TO-DO: deze moet nog private!!

        view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView episodetitleholder = v.findViewById(R.id.EpisodeTitleView);
                String episodetitle = episodetitleholder.getText().toString();
                String imdbid;
                for (int x = 0; x < episodeitems.size(); x++) {
                    if (episodeitems.get(x).getTitle() == episodetitle){
                        imdbid = episodeitems.get(x).getImdbid();
                        EpisodeDetailsFragment fragment = new EpisodeDetailsFragment();
                        Bundle args = new Bundle();
                        TextView lol = getView().findViewById(R.id.SerieNameInfo);
                        args.putString("title", lol.getText().toString());
                        args.putString("imdbid", imdbid);
                        fragment.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                    }
                }
                return true;
            }
        });
    }

    // TO-DO: deze moet private worden
    public class onChildClickListener{

    }


    /**
     * Parses the JSON respons when requesting details about an specifid season and puts
     * all the episode information in an arraylist
     */
    public void parseJSONSeasons(String response, Integer seasonnumber) {
        try {
            // Get the JSONArray with the episodes
            JSONObject responsedata = new JSONObject(response);
            JSONArray data = responsedata.getJSONArray("Episodes");

            // Get all the different characteristics of the episode
            for (int i = 0; i < data.length(); i++) {
                String title = data.getJSONObject(i).getString("Title");
                String released = data.getJSONObject(i).getString("Released");
                Integer episode = Integer.valueOf(data.getJSONObject(i).getString("Episode"));

                // TO-DO: Dit moet een string worden want geeft double error als er geen rating is
                String imdbrating = data.getJSONObject(i).getString("imdbRating");
                String imdbid = data.getJSONObject(i).getString("imdbID");

                // Make the information userfull by making an class object en put alle the episodes in the list
                Episode episodeinfo = new Episode(title, released, episode, imdbrating, imdbid, seasonnumber);
                addToEpisodes(episodeinfo);
            }
            Log.d("yyyyyxxxxx", String.valueOf(episodeitems.size()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("yyyyyxxxxx", String.valueOf(episodeitems.toString()));
    }

    public void addToEpisodes(Episode episodeinfo){
        episodeitems.add(episodeinfo);
        Log.d("Tesstttttt23423", String.valueOf(episodeitems.size()));
    }

    /**
     * Parses the data when requesting data about the serie, also sends requests for all the seasons
     * @param response
     */
    public void parseJSONSerieDetails(String response) {
        try{
            JSONObject responsedata = new JSONObject(response);
            serieinfo = new Serie(responsedata.getString("Title"), responsedata.getString("Year"),
                    responsedata.getString("Released"), responsedata.getString("Runtime"),
                    responsedata.getString("Genre"), responsedata.getString("Director"),
                    responsedata.getString("Writer"), responsedata.getString("Plot"),
                    responsedata.getString("Language"), responsedata.getString("Country"),
                    responsedata.getString("Awards"), responsedata.getString("Poster"),
                    responsedata.getDouble("imdbRating"), responsedata.getString("imdbVotes"),
                    responsedata.getInt("totalSeasons")) ;
            totalseasons = responsedata.getInt("totalSeasons");
            serieName = responsedata.getString("Title");
            for (int i = 1; i <= serieinfo.getTotalSeasons(); i++) {
                String url = "http://www.omdbapi.com/?apikey=14f4cb52&i=" + imdbid + "&season=" + String.valueOf(i);
                Log.d("dsfsuewirewpirpwe", "hoiii222");
                getData(url, 2, i);
                SeasonList.add("Season " + String.valueOf(i));
            };
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fillTextviews() {
        Log.d("xxxxxxxooooo", serieinfo.getAwards());
        TextView nameview = getView().findViewById(R.id.SerieNameInfo);
        nameview.setText(serieinfo.getTitle());
        TextView releaseview = getView().findViewById(R.id.SerieReleaseInfo);
        releaseview.setText(serieinfo.getReleased());
    }


}